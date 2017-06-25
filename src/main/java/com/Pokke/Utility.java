package com.Pokke;

import java.io.IOException;
import java.sql.Timestamp;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

/**
 * Created by EZ193M on 6/16/17.
 */
public class Utility {

    public static String getTableNameFromFileName(String fileLocation) {
        String[] temp = fileLocation.split("/");
        String file = temp[temp.length - 1];
        int pos = file.indexOf('.');

        return file.substring(0, pos);
    }

    // Read the properties from the properties file
    public static Map<String, String> readProperties(String fileLocation) {
        Map<String, String> results = new HashMap<String, String>();
        try {
            InputStream in = Utility.class.getResourceAsStream(fileLocation);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            String current = null;

            while(null != (current = bf.readLine())) {
                String[] temp = current.split("=");
                results.put(temp[0], temp[1]);
            }

            bf.close();
        }
        catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return results;
    }

    public static Boolean schemaMatch(Set<String> tableCols, String sheetHeader, String cellSplitor) {
        String[] fields = sheetHeader.split(cellSplitor);
        for (String field : fields) {
            if (!tableCols.contains(field)) {
                return false;
            }
        }

        return true;
    }

    public static String getColNames(String sheetHeader, String cellSplitor) {
        return sheetHeader.replaceAll(cellSplitor, ",");
    }

    public static String getValues(List<String> excelData, Map<String, String> schema, String cellSplitor) throws Exception {
        String results = "";

        String[] header = excelData.get(0).split(cellSplitor);

        for(int i = 1; i < excelData.size(); ++i) {
            String[] cells = excelData.get(i).split(cellSplitor);
            String currentRow = "(";

            for(int j = 0; j < cells.length; ++j) {
                String cellType = schema.get(header[j]).toLowerCase();
                String cellValue = cells[j];

                if ("null".equals(cellValue.toLowerCase())) {
                    currentRow += "NULL,";
                    continue;
                }

                if (cellType.equals("varchar") || cellType.equals("bool")) {
                    currentRow += "'" + cellValue + "',";
                }
                else if (cellType.contains("int")) {
                    try {
                        currentRow += Math.round(Float.parseFloat(cellValue)) + ",";
                    }
                    catch (Exception e) {
                        throw new Exception("Can't parse " + cellValue + " to int");
                    }
                }
                else if (cellType.contains("float")) {
                    try {
                        currentRow += Float.parseFloat(cellValue) + ",";
                    }
                    catch (Exception e) {
                        throw new Exception("Can't parse " + cellValue + " to float");
                    }
                }
                else if (cellType.equals("bool")) {
                    try {
                        currentRow += Boolean.parseBoolean(cellValue) + ",";
                    }
                    catch (Exception e) {
                        throw new Exception("Only value 'true' or 'false', but value is " + cellValue);
                    }
                }
                else if (cellType.equals("timestamp")) {
                    try {
                        currentRow += Timestamp.valueOf(cellValue);
                    }
                    catch (Exception e) {
                        throw new Exception("Can't parse " + cellValue + " to timestamp");
                    }
                }
            }

            currentRow = currentRow.substring(0, currentRow.length() - 1);
            currentRow += ")";
            results += currentRow + ",";
        }

        return results.substring(0, results.length() - 1);
    }
}
