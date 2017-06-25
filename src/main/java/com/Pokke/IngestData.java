package com.Pokke;

import java.sql.SQLException;
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
        colNames = colNames.endsWith(",") ? colNames.substring(0, colNames.length() - 1) : colNames;
        String colValues = Utility.getValues(excelData, tableSchema, cellSplitor);

        return dao.insertOperation(tableName, colNames, colValues);
    }

    public static void main(String args[]) throws Exception {
        String[] excels = args[0].split(",");
        IngestData id = new IngestData();

        for (String table : excels) {
            String tableName = Utility.getTableNameFromFileName(table);
            System.out.println("Ingesting table " + tableName + "...");
            if (id.Ingest(table, 0)) {
                System.out.println(tableName + " ingestion success!");
            }
            else {
                System.out.println(tableName + " ingestion failed!");
            }
        }
    }

}
