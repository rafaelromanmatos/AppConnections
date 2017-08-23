package com.sce.app.connections;

import java.sql.*;

public class AccessHelper {

    private Connection cn;
    private Statement st;
    private String dir;

    public AccessHelper() {
    }

    public AccessHelper(String dir) {
        this.dir = dir;
    }

    private Connection getOpenConnection() throws Exception {
        try {
            cn = DriverManager.getConnection("jdbc:ucanaccess://" + dir);
        } catch (SQLException ex) {
            throw new Exception("Error  :  Ruta " + dir);
        }
        return cn;
    }

    public ResultSet executeQuery(String sentence) throws Exception {
        ResultSet rs = null;
        st = getOpenConnection().createStatement();
        try {
            rs = st.executeQuery(sentence);
        } catch (SQLException ex) {
            throw ex;
        }
        return rs;
    }

    public void closeConnection() throws Exception {
        st.close();
        cn.close();
    }
}
