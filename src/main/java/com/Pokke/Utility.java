package com.Pokke;

import java.io.IOException;
import java.util.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by EZ193M on 6/16/17.
 */
public class Utility {

    public static String getTableNameFromFileName(String fileLocation) {
        String[] temp = fileLocation.split("/");
        String file = temp[temp.length - 1];

        return file.split(".")[0];
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
        if(fields.length != tableCols.size()) {
            return false;
        }

        for (String current : fields) {
            if (!tableCols.contains(current)) {
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

            for(int j = 0; j < cells.length; ++i) {
                String cellType = schema.get(header[j]).toLowerCase();
                String cellValue = cells[j];

                if(cellType.equals("varchar")) {
                    currentRow += "'" + cellValue + "',";
                }
                else if(cellType.contains("int")) {
                    try {
                        currentRow += Integer.parseInt(cellValue) + ",";
                    }
                    catch (Exception e) {
                        throw new Exception("Can't parse to int");
                    }
                }
            }

            currentRow = currentRow.substring(0, currentRow.length() - 1);
            currentRow += ")";
            results += currentRow;
        }

        return results;
    }
}
