package ua.ats.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
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
    private final static int HEADER_ROW = 8;

   /* public XSSFWorkbook book;
    public XSSFSheet sheet;*/

    int temp = 0;

    @Autowired
    private MainController mc;

    @Autowired
    private Calculation calc;

    @Autowired
    private InitParam ip;

    public void writeExel(List<Product> list) {

        BigDecimal cena;
        Cell cell;
        int colNum = 0;
        int startSummCell = list.get(0).getColumnNumberExel() + 1;
        for (Product product : list) {
            colNum = product.getColumnNumberExel() + 1;
            cena = product.getCena().multiply(product.getDiscount()).add(product.getColored());
            cell = ip.sheet.getRow(product.getColumnNumberExel()).getCell(PRICE_CELL);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(cena.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            cell = ip.sheet.getRow(product.getColumnNumberExel()).getCell(SUMM_CELL);
            String formula = "ROUND(L" + colNum + "*M" + colNum + ",2)";
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);
        }

        cell = ip.sheet.getRow(colNum).getCell(SUMM_CELL);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("SUM(N" + startSummCell + ":N" + colNum + ")");

        cell = ip.sheet.getRow(ip.lastRowNum ).getCell(SUMM_CELL);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("N" + (calc.getProfile().get(calc.getProfile().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getAccessories().get(calc.getAccessories().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getSealant().get(calc.getSealant().size() - 1).getColumnNumberExel() + 2) + "+" +
                "N" + (calc.getFurniture().get(calc.getFurniture().size() - 1).getColumnNumberExel() + 2));
    }

    public void decorateExel() {
        Cell cellColor;
        Font font = ip.book.createFont();
        font.setBold(true);

        XSSFCellStyle styleTop = ip.book.createCellStyle();
        styleTop.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleTop.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleTop.setBorderTop(BorderStyle.THICK);
        styleTop.setBorderRight(BorderStyle.THIN);
        styleTop.setAlignment(HorizontalAlignment.CENTER);
        styleTop.setFont(font);

        ip.sheet.getRow(HEADER_ROW).getCell(PRICE_CELL).setCellStyle(styleTop);
        ip.sheet.getRow(HEADER_ROW).getCell(SUMM_CELL).setCellStyle(styleTop);

        XSSFCellStyle styleBottom = ip.book.createCellStyle();
        styleBottom.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleBottom.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleBottom.setBorderBottom(BorderStyle.THICK);
        styleBottom.setBorderRight(BorderStyle.THIN);
        styleBottom.setAlignment(HorizontalAlignment.CENTER);
        styleBottom.setFont(font);

        ip.sheet.getRow((HEADER_ROW + 1)).getCell(PRICE_CELL).setCellStyle(styleBottom);
        ip.sheet.getRow((HEADER_ROW + 1)).getCell(SUMM_CELL).setCellStyle(styleBottom);


        int end = ip.lastRowNum;


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


        if (temp > 0) {
            ip.sheet.addMergedRegion(new CellRangeAddress(end + 5, end + 5, 6, 10));
        }
        temp++;
        Row row = ip.sheet.createRow(end + 5);
        cellColor = row.createCell(6);

        if (mc.totalColor != null && !(mc.totalColor.compareTo(BigDecimal.ZERO) == 0) || mc.totalColor != null
                || (mc.totalColor != null && !mc.colorInCena.isSelected() && mc.totalColor.compareTo(BigDecimal.ZERO) > 0)) {
            XSSFCellStyle style = ip.book.createCellStyle();
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        /*style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);*/
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font);
            cellColor.setCellStyle(style);
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("Покраска в RAL составляет " + mc.totalColor + " USD");
        }
        else {
            cellColor.setCellType(CellType.STRING);
            cellColor.setCellValue("");
        }
        ip.sheet.getRow(end + 2).getCell(3).setCellValue("");
        ip.sheet.getRow(end + 2).getCell(6).setCellValue("");
    }
}
