package ua.ats.logic;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Calculation {

    private static final Path DIR = Paths.get("d://alumotr");
    private byte type;
    private final static int START_ROW = 11;
    private final static int START_CELL = 7;

    public Row getStartRow() {
        Row row = null;
        File file;

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(DIR, "*.{xlsx,xls}")) {

            for (Path entry: stream) {
                file = entry.toFile();
                String str = file.getName();
                if (str.endsWith(".xlsx")) {
                    XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(file));
                    XSSFSheet sheet = book.getSheetAt(0);
                    row = sheet.getRow(START_ROW);
                }
                if (str.endsWith(".xls")) {
                    HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
                    HSSFSheet sheet = book.getSheetAt(0);
                    row = sheet.getRow(START_ROW);
                }
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return row;
    }


    public void foo() {
        Row row = getStartRow();



    }
}
