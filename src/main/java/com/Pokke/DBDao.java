package com.Pokke;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by EZ193M on 6/15/17.
 */
public class DBDao {

    private Connection conn = null;
    private Statement stmt = null;

    // Constructor
    public DBDao(String className, String url, String port, String dbName, String userName, String password) throws SQLException {
        if(null == conn) {
            conn = DBConnection.getConn(className, url, port, dbName, userName, password);
        }

        if(null == stmt) {
            stmt = conn.createStatement();
        }
    }

    // Get a table column names and types, throw exception if table doesn't exist
    public Map<String, String> getTableSchema(String tableName) throws Exception {
        Map<String, String> schema = new HashMap<String, String>();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, null);
            while (rs.next()) {
                schema.put(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
            }
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        if(schema.isEmpty()) {
            throw new Exception(tableName + " TABLE NOT FOUND!");
        }

        return schema;
    }

    // Insert Operation
    public Boolean insertOperation(String tableName, String colNames, String values) {
        int result = 0;
        String query = "INSERT INTO " + tableName + " (" + colNames + ") VALUES " + values + ";";
        try {
            result = stmt.executeUpdate(query);
            conn.commit();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return result == 0 ? false : true;
    }

    // Close the statement and connection object
    public void close() {
        try {
            stmt.close();
            conn.close();
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
