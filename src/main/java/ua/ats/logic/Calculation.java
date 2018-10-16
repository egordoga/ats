package ua.ats.logic;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.entity.Product;
import ua.ats.service.ProductService;
import ua.ats.util.InitParam;
import ua.ats.view.MainController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Calculation {

    private List<Product> profile = new ArrayList<>();
    private List<Product> accessories = new ArrayList<>();
    private List<Product> sealant = new ArrayList<>();
    private List<Product> furniture = new ArrayList<>();
    private List<Product> matInstall = new ArrayList<>();

    public XSSFWorkbook book;
    public XSSFSheet sheet;

    private final static int START_ROW = 11;
    private final static int NAME_CELL = 7;
    private final static int ARTICUL_CELL = 4;

    private final static BigDecimal HALF = new BigDecimal("0.5");

    private List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");

    public StringBuilder noFind = new StringBuilder();
    public int lastRowNum;

    @Autowired
    private MainController mc;

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
                if (row.getCell(11) != null) {
                    product.setQuantity(new BigDecimal(String.valueOf(row.getCell(11).getNumericCellValue())));
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


    public void rewriteByPrice(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                BigDecimal cost = product.getPrice().divide(new BigDecimal("1.2"), 3, BigDecimal.ROUND_HALF_UP);
                product.setCena(cost.multiply(markup).multiply(mc.discountProfile));
                if (mc.cbColorInCena.isSelected()) {
                    product.setSum((product.getCena().multiply(product.getQuantity()).add(product.getColorSum()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    product.setSum((product.getCena().multiply(product.getQuantity()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        mc.totalProfile = profile.stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void rewriteByWeight(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                BigDecimal cost;
                if (product.getColor() == 1) {
                    cost = product.getWeight().multiply(InitParam.costAlumWhite).multiply(new BigDecimal("1.01")
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    cost = product.getWeight().multiply(InitParam.costAlum).multiply(new BigDecimal("1.01"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                product.setCena(cost.multiply(markup).multiply(mc.discountProfile));
                if (mc.cbColorInCena.isSelected()) {
                    product.setSum((product.getCena().multiply(product.getQuantity()).add(product.getColorSum()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    product.setSum((product.getCena().multiply(product.getQuantity()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
            mc.totalProfile = profile.stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void rewriteByCost(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                product.setCena(product.getCost().multiply(markup).multiply(mc.discountProfile));
                if (mc.cbColorInCena.isSelected()) {
                    product.setSum((product.getCena().multiply(product.getQuantity()).add(product.getColorSum()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    product.setSum((product.getCena().multiply(product.getQuantity()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        mc.totalProfile = profile.stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void rewriteProfile() {
        for (Product product : profile) {

            product.setDiscount(mc.discountProfile);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountProfile)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteAccessories() {
        for (Product product : accessories) {
            product.setDiscount(mc.discountAccessories);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountAccessories)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteSealant() {
        for (Product product : sealant) {
            product.setDiscount(mc.discountSealant);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountSealant)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteFurniture() {
        for (Product product : furniture) {
            BigDecimal cost;
            if ("EUR".equals(product.getCurrency().getName())) {
                cost = product.getPrice().multiply(InitParam.crossRate);
            } else {
                cost = product.getPrice();
            }
            product.setDiscount(mc.discountFurniture);
            product.setSum(cost.multiply(product.getQuantity()).multiply(mc.discountFurniture)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }


    public void rewriteColorTotal() {
        mc.totalColor = BigDecimal.ZERO;

        BigDecimal colProf = profile.stream().map(Product::getColorSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal colFurn = furniture.stream().map(Product::getColorSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        mc.totalColor = colProf.add(colFurn);

        mc.lblTotColor.setText(mc.totalColor.toString());
    }

    public void settingColorSum(int colorType) {
        for (Product product : profile) {
            if (product.getColor() == 1) {
                product.setColorSum(BigDecimal.ZERO);
                product.setColored(BigDecimal.ZERO);
                switch (colorType) {
                    case 1:
                        product.setColored(mc.colored.multiply(product.getPerimeter()));
                        product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                .setScale(2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 2:
                        if (product.getBicolorWhiteIn() == 1) {
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));

                            System.out.println(product);
                        }
                        if (product.getBicolor() == 1) {
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));

                            System.out.println(product);
                        }
                        break;
                    case 3:
                        if (product.getBicolorWhiteOut() == 1) {
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                        if (product.getBicolor() == 1) {
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                        break;
                    case 4:
                        if (product.getBicolorWhiteOut() == 1 || product.getBicolorWhiteIn() == 1) {
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (product.getBicolor() == 1) {
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                        break;
                    case 5:
                        product.setColored(product.getCena().multiply(new BigDecimal("0.1")));
                        product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                .setScale(2, BigDecimal.ROUND_HALF_UP));
                        break;
                }
            }
        }

        if (colorType != 0) {
            for (Product product : furniture) {
                if (product.getColor() == 1) {
                    product.setColored(InitParam.colorFurn.divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP));
                    product.setColorSum((product.getQuantity().multiply(product.getColored()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
    }

    public void removeColorSum() {
        profile.forEach(j -> j.setColorSum(BigDecimal.ZERO));
        furniture.forEach(j -> j.setColorSum(BigDecimal.ZERO));
    }

    public void removeColorFromCena() {
        profile.forEach(p -> p.setSum(p.getSum().subtract(p.getColorSum())));
        furniture.forEach(p -> p.setSum(p.getSum().subtract(p.getColorSum())));
        mc.checkColorInCena = false;
    }

    public void addColorInCena() {
        profile.forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        furniture.forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        mc.checkColorInCena = true;
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


    public List<Product> getProfile() {
        return profile;
    }

    public List<Product> getAccessories() {
        return accessories;
    }

    public List<Product> getSealant() {
        return sealant;
    }

    public List<Product> getFurniture() {
        return furniture;
    }

    public List<Product> getMatInstall() {
        return matInstall;
    }
}
