package ua.ats.util;

import javafx.scene.control.RadioButton;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;
import ua.ats.view.MainController;

import java.math.BigDecimal;
import java.util.List;

@Component
public class WriteResult {

    private static final int PRICE_CELL = 12;
    private static final int SUMM_CELL = 13;
    private static final int COLOR_CELL = 10;
    private final static int HEADER_ROW = 8;

    private String strColor;
    private String strColorSecond;

   /* public XSSFWorkbook book;
    public XSSFSheet sheet;*/

    int temp = 0;

    @Autowired
    private MainController mc;

    @Autowired
    private Calculation calc;

    @Autowired
    private InitParam ip;

    public void writeExcel(List<Product> list) {

        Font font = calc.book.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);

        XSSFCellStyle styleInv = calc.book.createCellStyle();
        styleInv.setFont(font);

        BigDecimal cena;
        Cell cell;
        int colNum = 0;
        int startSummCell = list.get(0).getColumnNumberExel() + 1;
        for (Product product : list) {
            colNum = product.getColumnNumberExel() + 1;
            cena = product.getCena().multiply(product.getDiscount()).add(product.getColored());
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(PRICE_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(cena.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(SUMM_CELL);
            String formula = "ROUND(L" + colNum + "*M" + colNum + ",2)";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);

            if (mc.invoice.isSelected()) {
                cell = calc.sheet.getRow(product.getColumnNumberExel()).createCell(SUMM_CELL + 4);
                formula = "ROUND(M" + colNum + "*" + InitParam.rateUsd + ",2)";
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula(formula);
                cell.setCellStyle(styleInv);

                cell = calc.sheet.getRow(product.getColumnNumberExel()).createCell(SUMM_CELL + 5);
                formula = "ROUND(M" + colNum + "*" + InitParam.rateUsd + "/1.2,2)";
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula(formula);
                cell.setCellStyle(styleInv);
            } else {
                cell = calc.sheet.getRow(product.getColumnNumberExel()).createCell(SUMM_CELL + 4);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("");

                cell = calc.sheet.getRow(product.getColumnNumberExel()).createCell(SUMM_CELL + 5);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("");
            }
        }

        cell = calc.sheet.getRow(colNum).getCell(SUMM_CELL);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(N" + startSummCell + ":N" + colNum + ")");


    }

    public void writeExcelFurnZero() {
        Cell cell;
        int colNum;
        for (Product product : calc.getFurniture()) {
            colNum = product.getColumnNumberExel() + 1;
            //cena = product.getCena().multiply(product.getDiscount()).add(product.getColored());
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(PRICE_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(0);
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(SUMM_CELL);
            String formula = "ROUND(L" + colNum + "*M" + colNum + ",2)";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);
        }
    }

    public void writeExcelAllSum() {
        Cell cell = calc.sheet.getRow(calc.lastRowNum).getCell(SUMM_CELL);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("N" + (calc.getProfile().get(calc.getProfile().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getAccessories().get(calc.getAccessories().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getSealant().get(calc.getSealant().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getFurniture().get(calc.getFurniture().size() - 1).getColumnNumberExel() + 2));
    }

    public void decorateExcel() {
        Cell cellColor;
        Font font = calc.book.createFont();
        font.setBold(true);

        XSSFCellStyle styleTop = calc.book.createCellStyle();
        styleTop.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleTop.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleTop.setBorderTop(BorderStyle.THICK);
        styleTop.setBorderRight(BorderStyle.THIN);
        styleTop.setAlignment(HorizontalAlignment.CENTER);
        styleTop.setFont(font);

        calc.sheet.getRow(HEADER_ROW).getCell(PRICE_CELL).setCellStyle(styleTop);
        calc.sheet.getRow(HEADER_ROW).getCell(SUMM_CELL).setCellStyle(styleTop);

        XSSFCellStyle styleBottom = calc.book.createCellStyle();
        styleBottom.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleBottom.setBorderBottom(BorderStyle.THICK);
        styleBottom.setBorderRight(BorderStyle.THIN);
        styleBottom.setAlignment(HorizontalAlignment.CENTER);
        styleBottom.setFont(font);

        calc.sheet.getRow((HEADER_ROW + 1)).getCell(PRICE_CELL).setCellStyle(styleBottom);
        calc.sheet.getRow((HEADER_ROW + 1)).getCell(SUMM_CELL).setCellStyle(styleBottom);


        int end = calc.lastRowNum;
        calc.sheet.getRow(end + 2).getCell(3).setCellValue("");
        calc.sheet.getRow(end + 2).getCell(6).setCellValue("");



        /*for (int j = 11; j < i; j++) {
            if (calc.sheet.getRow(j) == null) {
                calc.sheet.shiftRows(j - 1, j, -1);
                System.out.println("SHIFT " + j);
            }
        }*/

       /* int s = calc.rowsForDel.size();
        int rowEnd;
        if (s > 0) {
            for (Integer rowDel : calc.rowsForDel) {
                System.out.println("rowDel " + rowDel);
                calc.sheet.shiftRows(rowDel + 1, end + 2, -1);
               // end--;
            }*/
           /* for (int j = 0; j < s; j++) {
                if (j > s - 1) {
                    rowEnd = calc.rowsForDel.get(j + 1);
                } else {
                    rowEnd = i + 2;
                }
                calc.sheet.shiftRows(calc.rowsForDel.get(j + 1), rowEnd, -1);
                i--;
            }*/
        //}


       /* if (temp > 0) {
            calc.sheet.addMergedRegion(new CellRangeAddress(end + 5, end + 5, 6, 10));
        }
        temp++;*/
        Font fontColor = calc.book.createFont();
        fontColor.setBold(true);
        fontColor.setColor(IndexedColors.RED.getIndex());
        Row row = calc.sheet.createRow(end + 3);
        cellColor = row.createCell(6);

        /*if (mc.totalColor != null && !(mc.totalColor.compareTo(BigDecimal.ZERO) == 0) || mc.totalColor != null
                || (mc.totalColor != null && !mc.colorInCena.isSelected() && mc.totalColor.compareTo(BigDecimal.ZERO) > 0)) {*/


        if (mc.totalColor != null && mc.totalColor.compareTo(BigDecimal.ZERO) > 0 && !mc.colorInCena.isSelected()) {
            XSSFCellStyle style = calc.book.createCellStyle();

            style.setFont(fontColor);
            cellColor.setCellStyle(style);
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("Покраска в RAL составляет " + mc.totalColor + " USD");
        } else {
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("");
        }

    }

    public void changeColorColumn() {

        if (mc.noRal.isSelected() || !mc.colorInCena.isSelected()) {
            for (Product product : calc.getProfile()) {
                if (product.getColor() == 1) {
                    calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL9016");
                }
            }
            for (Product product : calc.getFurniture()) {
                if (product.getColor() == 1) {
                    calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL9016");
                }
            }
            return;
        }

        Row row;
        switch (((RadioButton) mc.color.getSelectedToggle()).getId()) {
            case "ral":
                if (!(mc.ralNumber == null || "".equals(mc.ralNumber.getText()))) {
                   // strColor = "RAL" + mc.ralNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL" + mc.ralNumber.getText());
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL" + mc.ralNumber.getText());
                        }
                    }
                } else return;
                break;
            case "ral9006":
                //strColor = "RAL9006";
                for (Product product : calc.getProfile()) {
                    if (product.getColor() == 1) {
                        calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL9006");
                    }
                }
                for (Product product : calc.getFurniture()) {
                    if (product.getColor() == 1) {
                        calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL9006");
                    }
                }
                break;
            case "biIn":
                if (!(mc.ralBi1Number == null || "".equals(mc.ralBi1Number.getText()))) {
                    //strColor = "RAL" + mc.ralNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBi1Number.getText() + "/9016");
                            } else if (product.getBicolorWhiteIn() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBi1Number.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.ralBi1Number.getText());
                        }
                    }
                } else return;
                break;

            case "biOut":
                if (!(mc.ralBi1Number == null || "".equals(mc.ralBi1Number.getText()))) {
                    //strColor = "RAL" + mc.ralNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL9016/" + mc.ralBi1Number.getText());
                            } else if (product.getBicolorWhiteOut() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBi1Number.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.ralBi1Number.getText());
                        }
                    }
                } else return;
                break;

            case "bi2":
                if (!(mc.ralBiInNumber == null || "".equals(mc.ralBiInNumber.getText()))) {
                    //strColor = "RAL" + mc.ralNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBiOutNumber.getText() + "/" + mc.ralBiOutNumber.getText());
                            } else if (product.getBicolorWhiteOut() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBiInNumber.getText());
                            } else if (product.getBicolorWhiteIn() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.ralBiOutNumber.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.ralBiOutNumber.getText() + "/" + mc.ralBiOutNumber.getText());
                        }
                    }
                } else return;
                break;
        }

    }
}
