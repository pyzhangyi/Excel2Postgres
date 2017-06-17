package com.Pokke;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by EZ193M on 6/15/17.
 */
public class ExcelUtility {

    private final static String CELL_SPLITOR = "~@~";

    public List<String> readExcelFile(String excelLocation, int sheetNo) {

        List<String> results = new ArrayList<String>();

        try {
            FileInputStream excelFile = new FileInputStream(new File(excelLocation));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet selectedSheet = workbook.getSheetAt(sheetNo);
            Iterator<Row> it = selectedSheet.iterator();

            while (it.hasNext()) {

                Row row = it.next();
                Iterator<Cell> cellItor = row.cellIterator();
                String current = "";

                while (cellItor.hasNext()) {
                    Cell currentCell = cellItor.next();
                    CellType cellType = currentCell.getCellTypeEnum();

                    if (CellType.STRING == cellType) {
                        current += currentCell.getStringCellValue() + CELL_SPLITOR;
                    }
                    else if (CellType.NUMERIC == cellType) {
                        current += currentCell.getNumericCellValue() + CELL_SPLITOR;
                    }
                    else if (CellType.BOOLEAN == cellType) {
                        current += currentCell.getBooleanCellValue() + CELL_SPLITOR;
                    }
                    else if (CellType.BLANK == cellType) {
                        current += "NULL" + CELL_SPLITOR;
                    }
                }

                results.add(current.substring(0, current.length()));
            }
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return results;
    }

    // Return the cell splitor
    public String getCellSplitor() {
        return CELL_SPLITOR;
    }
}
