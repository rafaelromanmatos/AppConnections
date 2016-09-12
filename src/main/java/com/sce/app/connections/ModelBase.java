package com.sce.app.connections;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ModelBase {

    public MysqlHelper oMysqlHelper;

    protected Connection getConnection() throws RuntimeException {
        try {
            return oMysqlHelper.getDataSource().getConnection();
        } catch (SQLException se) {
            System.err.println("Error: cerrarConexion: " + se);
            return null;
        }
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

}
