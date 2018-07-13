package ua.ats.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;
import ua.ats.view.MainController;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class InitParam {

    public static BigDecimal costAlumWhite;     // = new BigDecimal("3.97");
    public static BigDecimal costAlumWhithoutColor;     // = new BigDecimal("3.97");
    public static BigDecimal costAlum;          // = new BigDecimal("3.97");
    public static BigDecimal rateEur;           // = new BigDecimal("1.172");
    public static BigDecimal rateUsd;           // = new BigDecimal("27.1");
    public static BigDecimal crossRate;           // = new BigDecimal("27.1");
    public static BigDecimal color;             // = new BigDecimal("160");
    public static BigDecimal color9006;         // = new BigDecimal("170");
    public static BigDecimal colorFurn;         // = new BigDecimal("50");
    public static BigDecimal bicolor;           // = new BigDecimal("190");
    public static BigDecimal bicolorWithWhite;  // = new BigDecimal("380");
    public static BigDecimal dekor;             // = new BigDecimal("630");

    public XSSFWorkbook book;
    public XSSFSheet sheet;

    private final static int START_ROW = 11;
    private final static int NAME_CELL = 7;
    private final static int ARTICUL_CELL = 4;

    private List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");
    private List<Integer> rowsForDel = new ArrayList<>();

    private StringBuilder noFind = new StringBuilder();
    public int lastRowNum;


    private static final Path PROP = Paths.get("d://alumotr/properties.txt");

    @Autowired
    private static MainController mc;

    @Autowired
    private Calculation calc;


    public static void initParam() {
        try {
            List<String> list = Files.readAllLines(PROP);

            costAlumWhite = new BigDecimal(list.get(1));
            costAlum = new BigDecimal(list.get(3));
            rateUsd = new BigDecimal(list.get(5));
            rateEur = new BigDecimal(list.get(7));
            color = new BigDecimal(list.get(9));
            color9006 = new BigDecimal(list.get(11));
            bicolorWithWhite = new BigDecimal(list.get(13));
            bicolor = new BigDecimal(list.get(15));
            dekor = new BigDecimal(list.get(17));
            colorFurn = new BigDecimal(list.get(19));

            initCross();

            mc.markupF50 = new BigDecimal("1.2");
            mc.markupW70 = new BigDecimal("1.2");
            mc.markupL45 = new BigDecimal("1.2");
            mc.discountProfile = new BigDecimal("1");
            mc.discountAccessories = new BigDecimal("1");
            mc.discountSealant = new BigDecimal("1");
            mc.discountFurniture = new BigDecimal("1");
            mc.colored = BigDecimal.ZERO;
            mc.coloredBicolor = BigDecimal.ZERO;
            mc.costTypeF50 = 1;
            mc.costTypeW70 = 1;
            mc.costTypeL45 = 1;
            mc.checkColorInCena = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initCross() {
        crossRate = rateEur.divide(rateUsd, 3, BigDecimal.ROUND_HALF_UP);
    }

    public void fillLists(ProductRepository productRepository) {

        int i = START_ROW - 1;
        Row row;
        String name;
        String articul;
        try {

            book = new XSSFWorkbook(new FileInputStream(mc.file));
            sheet = book.getSheetAt(0);
            //row = sheet.getRow(START_ROW);

            while (true) {
                i++;
                row = sheet.getRow(i);
                if (row.getCell(NAME_CELL) == null) {
                    //sheet.removeRow(row);
                    //i--;
                  /*  sheet.shiftRows(i, i + 1, -1);
                    try {
                        FileOutputStream outFile = new FileOutputStream(mc.file);
                        book.write(outFile);
                        outFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    rowsForDel.add(i);
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
                        articul = String.valueOf((int) (row.getCell(ARTICUL_CELL).getNumericCellValue()));
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
                Product product = productRepository.findProductByArticul(articul);

                if (product == null) {
                    noFind.append(name).append("\n");
                }


                if (!(product == null)) {
                    product.setColumnNumberExel(i);
                    product.setColorSum(BigDecimal.ZERO);
                    product.setPreviousCena(BigDecimal.ZERO);
                    product.setColored(BigDecimal.ZERO);
                    product.setDiscount(BigDecimal.ONE);
                    if (row.getCell(11) != null) {
                        product.setQuantity(new BigDecimal(String.valueOf(row.getCell(11).getNumericCellValue())));
                        product.setColorSum(BigDecimal.ZERO);
                    } else {
                        product.setQuantity(BigDecimal.ZERO);
                        product.setColorSum(BigDecimal.ZERO);
                    }


                    switch (product.getSection().getName()) {
                        case "профиль":
                            calc.getProfile().add(product);
                            initSumms(product);
                            break;
                        case "комплектующие":
                            calc.getAccessories().add(product);
                            initSumms(product);
                            break;
                        case "уплотнители":
                            calc.getSealant().add(product);
                            initSumms(product);
                            break;
                        case "фурнитура":
                            calc.getFurniture().add(product);
                            if ("EUR".equals(product.getCurrency().getName())) {
                                product.setCena(product.getPrice().multiply(InitParam.crossRate));
                            } else {
                                product.setCena(product.getPrice());
                            }
                            product.setSum(product.getCena().multiply(product.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            break;
                    }
                }
            }
            lastRowNum = i;

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (noFind.length() != 0) {
            mc.showNoFind(noFind.toString());
        }
    }

    private void initSumms(Product product) {
        product.setCena(product.getPrice());
        product.setSum(product.getCena().multiply(product.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP));
        product.setDiscountSum(BigDecimal.ZERO);
        product.setColorSum(BigDecimal.ZERO);
        product.setPreviousCena(BigDecimal.ZERO);
    }

    public void deleteEmptyRow() {
        try {
            book = new XSSFWorkbook(new FileInputStream(mc.file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = book.getSheetAt(0);

        int i = START_ROW;

        //while (ca)
    }

    public void rallBack() {

    }
}
