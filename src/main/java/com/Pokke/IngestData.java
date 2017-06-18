package com.Pokke;

import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

/**
 * Created by EZ193M on 6/16/17.
 */
public class IngestData {

    private final static String PROPERTIES_LOCATION = "/Properties/Ingest.properties";
    private static Map<String, String> properties = null;
    private static DBDao dao = null;

    public IngestData() throws SQLException {
        if (null == properties) {
            properties = Utility.readProperties(PROPERTIES_LOCATION);
        }

        if (null == dao) {
            dao = new DBDao(properties.get("className"), properties.get("url"), properties.get("port"), properties.get("dbName"), properties.get("userName"), properties.get("password"));
        }
    }

    public Boolean Ingest(String fileLocation, int sheetNo) throws Exception {
        String tableName = Utility.getTableNameFromFileName(fileLocation);
        Map<String, String> tableSchema = dao.getTableSchema(tableName);
        ExcelUtility eu = new ExcelUtility();
        List<String> excelData = eu.readExcelFile(fileLocation, sheetNo);
        String cellSplitor = eu.getCellSplitor();

        if (!Utility.schemaMatch(tableSchema.keySet(), excelData.get(0), cellSplitor)) {
            throw new Exception("Excel schema cannot match table schema!");
        }

        String colNames = Utility.getColNames(excelData.get(0), cellSplitor);
        String colValues = Utility.getValues(excelData, tableSchema, cellSplitor);

        return dao.insertOperation(tableName, colNames, colValues);
    }

    public static void main(String[] args) throws Exception {
        String[] tables = new String[] {"category", "address", "diploma", "img", "institute", "contact_info", "post_type", "social_meta", "role", "title", "status", "post", "user", "loc_post", "like_reply", "loc_post_key_word", "location", "user_academic", "user_contact_info", "user_ref", "user_social_meta", "coordinate", "like_post", "key_word"};
        new IngestData();
        Set<String> types = new HashSet<String>();

        for(String table : tables) {
            Map<String, String> schema = dao.getTableSchema(table);
            for(String key : schema.keySet()) {
                types.add(schema.get(key));
            }
        }

        for(String temp : types) {
            System.out.println(temp);
        }
    }

}
