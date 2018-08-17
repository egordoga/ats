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
    private static final int QUANT_CELL = 11;
    private final static int HEADER_ROW = 8;


    @Autowired
    private MainController mc;

    @Autowired
    private Calculation calc;


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
            if (mc.cbColorInCena.isSelected()) {
                cena = product.getCena().multiply(product.getDiscount()).add(product.getColored());
            } else {
                cena = product.getCena().multiply(product.getDiscount());
            }
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(PRICE_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(cena.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(SUMM_CELL);
            String formula = "ROUND(L" + colNum + "*M" + colNum + ",2)";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);

            if (mc.cbInvoice.isSelected()) {
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
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(PRICE_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(0);
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(SUMM_CELL);
            String formula = "ROUND(L" + colNum + "*M" + colNum + ",2)";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);
        }
    }

    public void writeQuantitySealent() {
        Cell cell;
        for (Product product : calc.getSealant()) {
            cell = calc.sheet.getRow(product.getColumnNumberExel()).getCell(QUANT_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(product.getQuantity().doubleValue());
        }
    }

    public void writeExcelAllSum() {
        Cell cell = calc.sheet.getRow(calc.lastRowNum).getCell(SUMM_CELL);
        cell.setCellType(CellType.FORMULA);

        String totalFormula = "N" + (calc.getProfile().get(calc.getProfile().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getAccessories().get(calc.getAccessories().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getSealant().get(calc.getSealant().size() - 1).getColumnNumberExel() + 2);

        if (calc.getFurniture().size() > 0) {
            totalFormula += "+N" + (calc.getFurniture().get(calc.getFurniture().size() - 1).getColumnNumberExel() + 2);
        }

        if (calc.getMatInstall().size() > 0) {
           /* cell.setCellFormula("N" + (calc.getProfile().get(calc.getProfile().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + (calc.getAccessories().get(calc.getAccessories().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + (calc.getSealant().get(calc.getSealant().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + (calc.getFurniture().get(calc.getFurniture().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + calc.lastRowNum);
        } else {
            cell.setCellFormula("N" + (calc.getProfile().get(calc.getProfile().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + (calc.getAccessories().get(calc.getAccessories().size() - 1).getColumnNumberExel() + 2) + "+" +
                    "N" + (calc.getSealant().get(calc.getSealant().size() - 1).getColumnNumberExel() + 2) + "+" +*/


                    totalFormula += "+N" + calc.lastRowNum;
        }

        cell.setCellFormula(totalFormula);
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
        if (calc.sheet.getRow(end + 2) != null) {
            calc.sheet.getRow(end + 2).getCell(3).setCellValue("");
            calc.sheet.getRow(end + 2).getCell(6).setCellValue("");
        }

        Font fontColor = calc.book.createFont();
        fontColor.setBold(true);
        fontColor.setColor(IndexedColors.RED.getIndex());
        Row row = calc.sheet.createRow(end + 3);
        cellColor = row.createCell(6);

        if (mc.totalColor != null && mc.totalColor.compareTo(BigDecimal.ZERO) > 0 && !mc.cbColorInCena.isSelected()) {
            XSSFCellStyle style = calc.book.createCellStyle();

            style.setFont(fontColor);
            cellColor.setCellStyle(style);
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("Покраска в " + mc.strRalNumber + " составляет " + mc.totalColor.setScale(0, BigDecimal.ROUND_UP) + " USD");
        } else {
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("");
        }

    }


    public void changeColorColumn() {

        if (mc.rbNoRal.isSelected() || !mc.cbColorInCena.isSelected()) {
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

        switch (((RadioButton) mc.tgColor.getSelectedToggle()).getId()) {
            case "rbRal":
                if (!(mc.tfRalNumber == null || "".equals(mc.tfRalNumber.getText()))) {
                   // strColor = "RAL" + mc.tfRalNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL" + mc.tfRalNumber.getText());
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL).setCellValue("RAL" + mc.tfRalNumber.getText());
                        }
                    }
                } else return;
                break;
            case "rbRal9006":
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
            case "rbBiIn":
                if (!(mc.tfRalBi1Number == null || "".equals(mc.tfRalBi1Number.getText()))) {
                    //strColor = "RAL" + mc.tfRalNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBi1Number.getText() + "/9016");
                            } else if (product.getBicolorWhiteIn() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBi1Number.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.tfRalBi1Number.getText());
                        }
                    }
                } else return;
                break;

            case "rbBiOut":
                if (!(mc.tfRalBi1Number == null || "".equals(mc.tfRalBi1Number.getText()))) {
                    //strColor = "RAL" + mc.tfRalNumber.getText();
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL9016/" + mc.tfRalBi1Number.getText());
                            } else if (product.getBicolorWhiteOut() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBi1Number.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.tfRalBi1Number.getText());
                        }
                    }
                } else return;
                break;

            case "rbBi2":
                if (!(mc.tfRalBiInNumber == null || "".equals(mc.tfRalBiInNumber.getText()))) {
                    for (Product product : calc.getProfile()) {
                        if (product.getColor() == 1) {
                            if (product.getBicolor() == 1) {
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBiOutNumber.getText() + "/" + mc.tfRalBiInNumber.getText());
                            } else if (product.getBicolorWhiteOut() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBiInNumber.getText());
                            } else if (product.getBicolorWhiteIn() == 1){
                                calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                        .setCellValue("RAL" + mc.tfRalBiOutNumber.getText());
                            }
                        }
                    }
                    for (Product product : calc.getFurniture()) {
                        if (product.getColor() == 1) {
                            calc.sheet.getRow(product.getColumnNumberExel()).getCell(COLOR_CELL)
                                    .setCellValue("RAL" + mc.tfRalBiOutNumber.getText() + "/" + mc.tfRalBiInNumber.getText());
                        }
                    }
                } else return;
                break;
        }

    }
}
