package com.sce.app.connections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import org.apache.commons.dbcp.BasicDataSource;

public class MysqlHelper {

    public DataSource dataSource;

    private MysqlHelper(String serverIp, String puerto, String dbName, String user, String pass) {
        inicializaDataSource(serverIp, puerto, dbName, user, pass);
    }

    public static MysqlHelper getMysqlHelper(String serverIp, String puerto, String dbName, String user, String pass) {
        return new MysqlHelper(serverIp, puerto, dbName, user, pass);
    }

    private void inicializaDataSource(String serverIp, String puerto, String dbName, String user, String pass) {
        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            basicDataSource.setUrl("jdbc:mysql://" + serverIp + ":" + puerto + "/" + dbName + "?" + "allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useInformationSchema=true&noAccessToProcedureBodies=true");
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(pass);
            basicDataSource.setValidationQuery("SELECT 1");
            dataSource = basicDataSource;
        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public int executeNonQuery(String nameMethod, Object[] parameters) throws Exception {
        Connection cn = null;
        CallableStatement cmd = null;
        try {
            cn = dataSource.getConnection();
            cmd = cn.prepareCall(nameMethod);
            for (int i = 0; i < parameters.length; i++) {
                cmd.setObject(i + 1, parameters[i]);
            }
            return cmd.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (null != cmd) {
                cmd.close();
            }
            if (null != cn) {
                cn.close();
            }
        }
    }

    public <T> T show(Class clase, boolean execWithProc, String sql, Object[] parameters) {
        Connection cn = null;
        Statement st = null;
        CallableStatement cmd = null;
        ResultSet rs = null;
        T obj = null;
        String nameClase;
        try {
            obj = (T) Class.forName(clase.getName()).newInstance();
            nameClase = obj.getClass().getName();
            cn = getDataSource().getConnection();
            if (execWithProc) {
                cmd = cn.prepareCall(sql);
                for (int i = 0; i < parameters.length; i++) {
                    cmd.setObject(i + 1, parameters[i]);
                }
                rs = cmd.executeQuery();
            } else {
                st = cn.createStatement();
                rs = st.executeQuery(sql);
            }
            if (null != nameClase) {
                switch (nameClase) {
                    case "java.util.ArrayList": {
                        List<T> lst = new ArrayList<>();
                        Method[] metodos = obj.getClass().getMethods();
                        while (rs.next()) {
                            for (Method metodo : metodos) {
//                                if (metodo.getName().startsWith("set")) {
//                                    metodo.invoke(obj, rs.getString(metodo.getName().substring(3)));
//                                }
                                if (obj.getClass().getName().equals("java.util.ArrayList")) {
                                    obj = (T) rs.getString(1);
                                }
                            }
                            lst.add(obj);
                        }
                        obj = (T) lst;
                        break;
                    }
                    case "java.lang.String":
                        int cols = rs.getMetaData().getColumnCount();
                        String resp = "";
                        int reg = 0;
                        while (rs.next()) {
                            for (int i = 1; i <= cols; i++) {
                                if (reg == 0) {
                                    resp += (i != 1 ? "#,#" : "") + (!"".equals(rs.getString(i)) ? rs.getString(i) : "  ");
                                } else {
                                    resp += "#,#" + (!"".equals(rs.getString(i)) ? rs.getString(i) : "  ");
                                }
                            }
                            reg++;
                        }
                        obj = (T) resp;
                        break;
                    default: {
                        Method[] metodos = obj.getClass().getDeclaredMethods();
                        if (rs.next()) {
                            for (Method metodo : metodos) {
                                if (metodo.getName().startsWith("set")) {
                                    for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                                        if (metodo.getName().substring(3).equalsIgnoreCase(rs.getMetaData().getColumnName(j))) {
                                            if (null != rs.getObject(metodo.getName().substring(3))) {
                                                metodo.invoke(obj, rs.getObject(metodo.getName().substring(3)));
                                            } else {
                                                switch (rs.getMetaData().getColumnTypeName(j)) {
                                                    case "DOUBLE":
                                                        metodo.invoke(obj, 0.00);
                                                        break;
                                                    case "INT":
                                                        metodo.invoke(obj, 0);
                                                        break;
                                                    default:
                                                        if (!rs.getMetaData().getColumnTypeName(j).equals("DATETIME") && !rs.getMetaData().getColumnTypeName(j).equals("DATE")) {
                                                            metodo.invoke(obj, "");
                                                        }
                                                        break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            obj = (T) obj;
                        } else {
                            obj = null;
                        }
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (null != st) {
                try {
                    st.close();
                } catch (SQLException ex) {
                }
            }
            if (null != cmd) {
                try {
                    cmd.close();
                } catch (SQLException e) {
                }
            }
            if (null != cn) {
                try {
                    cn.close();
                } catch (SQLException e) {
                }
            }
        }
        return obj;
    }
}
