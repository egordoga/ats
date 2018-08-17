package ua.ats.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Temp {

    public static void main(String[] args) {
        final int NAME_CELL = 7;
        XSSFWorkbook book = null;
        XSSFSheet sheet = null;
        Row row = null;
        File file = new File("d://alumotr/1216_111.xlsx");
        try {
            book = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (book != null) {
            sheet = book.getSheetAt(0);
        }

        assert sheet != null;
        int lastRow;

        int i = 12;
        float heightRow = sheet.getRow(i).getHeightInPoints();
        List<Integer> rowsForDel = new ArrayList<>();

        while (true) {
            i++;
            row = sheet.getRow(i);
            System.out.println(i);
            if (row.getCell(NAME_CELL) == null) {
                lastRow = sheet.getLastRowNum();
                sheet.shiftRows(i + 1, lastRow, -1);

                rowsForDel.add(i);
                System.out.println(i + " EEE");
                row.setHeightInPoints(heightRow);
                continue;
            }

            row.setHeightInPoints(heightRow);
            if ("ИТОГО".equals(row.getCell(NAME_CELL).getStringCellValue())) {
                break;
            }

        }

       /* int k = 1;
        for (Integer aRowsForDel : rowsForDel) {
            lastRow = sheet.getLastRowNum();
            //sheet.removeRow(sheet.getRow(aRowsForDel));
            //sheet.addMergedRegion(new CellRangeAddress(aRowsForDel, aRowsForDel, 1, 3));
            sheet.shiftRows(aRowsForDel + k, lastRow, -1);
            k++;
        }*/


        try {
            FileOutputStream outFile = new FileOutputStream(file);
            book.write(outFile);
            book.close();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}