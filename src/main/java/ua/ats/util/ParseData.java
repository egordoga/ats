package ua.ats.util;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.entity.Product;
import ua.ats.service.ProductService;
import ua.ats.view.MainController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Component
public class ParseData {

    private List<Product> profile = new ArrayList<>();
    private List<Product> accessories = new ArrayList<>();
    private List<Product> sealant = new ArrayList<>();
    private List<Product> furniture = new ArrayList<>();
    private List<Product> matInstall = new ArrayList<>();

    private XSSFWorkbook book;
    private XSSFSheet sheet;

    private final static int START_ROW = 11;
    private final static int QUANTITY_CELL = 11;
    private final static int NAME_CELL = 7;
    private final static int ARTICUL_CELL = 4;

    private final static BigDecimal HALF = new BigDecimal("0.5");

    private final List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");

    private StringBuilder noFind = new StringBuilder();
    private int lastRowNum;

    private final MainController mc;

    @Autowired
    public ParseData(MainController mc) {
        this.mc = mc;
    }

    public void fillLists(ProductService productService) {

        int i = START_ROW - 1;
        Row row;
        String name;
        String articul;
        File file = mc.file;

        try {
            book = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = book.getSheetAt(0);

        prepareExcel();

        while (true) {
            i++;
            row = sheet.getRow(i);
            if (row.getCell(NAME_CELL) == null) {
                continue;
            }
            if (("ИТОГО".equals(row.getCell(NAME_CELL).getStringCellValue()))) {
                break;
            }


            if (row.getCell(NAME_CELL) != null && row.getCell(ARTICUL_CELL) != null) {
                name = row.getCell(NAME_CELL).getStringCellValue();
                if (row.getCell(ARTICUL_CELL).getCellTypeEnum() == CellType.STRING) {
                    articul = row.getCell(ARTICUL_CELL).getStringCellValue();
                } else {
                    articul = String.valueOf((long) (row.getCell(ARTICUL_CELL).getNumericCellValue()));
                }
            } else {
                continue;
            }

            if (noNeed.contains(name)) {
                continue;
            }

            if (row.getCell(ARTICUL_CELL) == null) {
                continue;
            }

            Product product = productService.findProductByArticul(articul);

            if (product == null) {
                noFind.append(name).append("\n");
            }


            if (!(product == null)) {
                product.setColumnNumberExel(i);
                product.setColorSum(BigDecimal.ZERO);
                product.setPreviousCena(BigDecimal.ZERO);
                product.setColored(BigDecimal.ZERO);
                product.setDiscount(BigDecimal.ONE);
                if (row.getCell(QUANTITY_CELL) != null) {
                    product.setQuantity(new BigDecimal(String.valueOf(row.getCell(QUANTITY_CELL).getNumericCellValue())));
                    product.setColorSum(BigDecimal.ZERO);
                } else {
                    product.setQuantity(BigDecimal.ZERO);
                    product.setColorSum(BigDecimal.ZERO);
                }


                switch (product.getSection().getName()) {
                    case "профиль":
                        profile.add(product);
                        initSumms(product);
                        break;
                    case "комплектующие":
                        accessories.add(product);
                        initSumms(product);
                        break;
                    case "уплотнители":
                        if (!("SYG23".equals(product.getArticul()) || "SYG42".equals(product.getArticul()))) {

                            if ("9FE/08".equals(product.getArticul())) {
                                product.setQuantity(product.getQuantity().setScale(0, BigDecimal.ROUND_UP));
                            } else {
                                product.setQuantity(roundFiveTen(product.getQuantity()));
                            }
                        }
                        sealant.add(product);
                        initSumms(product);
                        break;
                    case "фурнитура":
                        furniture.add(product);
                        if ("EUR".equals(product.getCurrency().getName())) {
                            product.setCena(product.getPrice().multiply(InitParam.crossRate));
                        } else {
                            product.setCena(product.getPrice());
                        }
                        product.setSum(product.getCena().multiply(product.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case "материалы для монтажа":
                        if ("6х60".equals(product.getArticul())) {
                            matInstall.add(product);
                            initSumms(product);
                        }
                }
            }
        }
        lastRowNum = i;

        if (noFind.length() != 0) {
            mc.showNoFind(noFind.toString());
        }
    }

    private void prepareExcel() {
        Row row;
        int lastRow;

        int i = 12;
        float heightRow = sheet.getRow(i).getHeightInPoints();

        while (true) {
            i++;
            row = sheet.getRow(i);

            if (row == null) {
                row = sheet.createRow(i);
            }

            if (row.getCell(NAME_CELL) == null) {
                lastRow = sheet.getLastRowNum();
                sheet.shiftRows(i + 1, lastRow, -1);
                i--;
                continue;
            }
            row.setHeightInPoints(heightRow);

            if ("ИТОГО".equals(row.getCell(NAME_CELL).getStringCellValue())) {
                break;
            }
        }
    }

    private void initSumms(Product product) {
        product.setCena(product.getPrice());
        product.setSum(product.getCena().multiply(product.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP));
        product.setDiscountSum(BigDecimal.ZERO);
        product.setColorSum(BigDecimal.ZERO);
        product.setPreviousCena(BigDecimal.ZERO);
    }

    private BigDecimal roundFiveTen(BigDecimal bd) {
        String str = bd.toString();
        if (!(str.contains(".") || str.contains(",")) && (str.endsWith("5") || str.endsWith("0"))) {
            return bd;
        }

        BigDecimal result;
        BigDecimal tempInt = bd.divide(BigDecimal.TEN, 0, BigDecimal.ROUND_DOWN);
        BigDecimal temp = bd.divide(BigDecimal.TEN, 2, BigDecimal.ROUND_HALF_UP).subtract(tempInt);
        result = temp.compareTo(HALF) < 0 ? (tempInt.add(HALF)).multiply(BigDecimal.TEN) : (tempInt.add(BigDecimal.ONE)).multiply(BigDecimal.TEN);
        return result;

    }
}
