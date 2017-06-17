package com.Pokke;

import java.sql.DriverManager;
import java.sql.Connection;

/**
 * Created by EZ193M on 6/15/17.
 */
public class DBConnection {

    private static Connection conn = null;

    protected DBConnection(String className, String url, String port, String dbName, String userName, String password) {
        try {
            Class.forName(className);
            String dbAddr = url + ":" + port + "/" + dbName;
            conn = DriverManager.getConnection(dbAddr, userName, password);
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static Connection getConn(String className, String url, String port, String dbName, String userName, String password) {
        if(null == conn) {
            new DBConnection(className, url, port, dbName, userName, password);
        }
        return conn;
    }
}