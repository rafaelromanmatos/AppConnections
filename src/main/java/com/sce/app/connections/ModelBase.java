package com.sce.app.connections;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;

public class ModelBase {

    public MysqlHelper oMysqlHelper;

    public MysqlHelper getoMysqlHelper() {
        return oMysqlHelper;
    }

    public void setoMysqlHelper(MysqlHelper oMysqlHelper) {
        this.oMysqlHelper = oMysqlHelper;
    }

    protected void cerrarConexion(Connection cn) throws RuntimeException {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
            }
        } catch (SQLException se) {
            System.err.println("Error: cerrarConexion: " + se);
        }
    }

    protected void cerrarResultSet(ResultSet rs) throws RuntimeException {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException se) {
            System.err.println("Error: cerrarResultSet: " + se);
        }
    }

    protected void cerrarStatement(Statement st) throws RuntimeException {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException se) {
            System.err.println("Error: cerrarStatement: " + se);
        }
    }

    protected void cerrarPrepared(PreparedStatement psmt) throws RuntimeException {
        try {
            if (psmt != null) {
                psmt.close();
            }
        } catch (SQLException se) {
            System.err.println("Error: cerrarPrepared: " + se);
        }
    }

    protected void cerrarCallable(CallableStatement callstmt) throws RuntimeException {
        try {
            if (callstmt != null) {
                callstmt.close();
            }
        } catch (SQLException se) {
            System.err.println("Error: cerrarCallable: " + se);
        }
    }

    protected Connection getConnection2() throws RuntimeException {
        try {
            return getoMysqlHelper().getDataSource().getConnection();
        } catch (SQLException se) {
            System.err.println("Error: cerrarConexion: " + se);
            return null;
        }
    }

    protected String show2(String sql) {
        return getoMysqlHelper().show(String.class, false, sql, new Object[]{});
    }

    protected List<Object> getSqlSelect2(int col, String sql) throws Exception {
        List<Object> lst = new ArrayList<>();
        Connection cn = null;
        ResultSet rs = null;
        PreparedStatement psmt = null;
        try {
            cn = getoMysqlHelper().dataSource.getConnection();
            psmt = cn.prepareStatement(sql);
            rs = psmt.executeQuery();
            int totCol = rs.getMetaData().getColumnCount();
            Object[] fil;
            while (rs.next()) {
                fil = new Object[col];
                for (int i = 0; i < totCol; i++) {
                    fil[i] = rs.getObject(i + 1);
                }
                lst.add(fil);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            cerrarConexion(cn);
            cerrarResultSet(rs);
            cerrarPrepared(psmt);
        }
        return lst;
    }

    protected void setDataJtable2(String sentence, JTable jtable) throws Exception {
        getoMysqlHelper().executeQueryTableModel("{ CALL spRunSentence(?) }", new Object[]{sentence}, jtable, false);
    }

}
