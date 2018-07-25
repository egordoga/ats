package ua.ats.logic;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
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


    public XSSFWorkbook book;
    public XSSFSheet sheet;

    private final static int START_ROW = 11;
    private final static int NAME_CELL = 7;
    private final static int ARTICUL_CELL = 4;

    private List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");
    private List<Integer> rowsForDel = new ArrayList<>();

    public StringBuilder noFind = new StringBuilder();
    public int lastRowNum;

    @Autowired
    private MainController mc;

    public void fillLists(ProductRepository productRepository) {

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
                        getProfile().add(product);
                        initSumms(product);
                        break;
                    case "комплектующие":
                        getAccessories().add(product);
                        initSumms(product);
                        break;
                    case "уплотнители":
                        getSealant().add(product);
                        initSumms(product);
                        break;
                    case "фурнитура":
                        getFurniture().add(product);
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


    public void rewriteByPrice(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                BigDecimal cost = product.getPrice().divide(new BigDecimal("1.2"), 3, BigDecimal.ROUND_HALF_UP);
                product.setCena(cost.multiply(markup).multiply(mc.discountProfile));
                if (mc.colorInCena.isSelected()) {
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
                if (mc.colorInCena.isSelected()) {
                    product.setSum((product.getCena().multiply(product.getQuantity()).add(product.getColorSum()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    product.setSum((product.getCena().multiply(product.getQuantity()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                ;
            }
            mc.totalProfile = profile.stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void rewriteByCost(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                product.setCena(product.getCost().multiply(markup).multiply(mc.discountProfile));
                if (mc.colorInCena.isSelected()) {
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

        mc.totColor.setText(mc.totalColor.toString());
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
                        }
                        if (product.getBicolor() == 1) {
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                            product.setColorSum((product.getQuantity().multiply(product.getColored()))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
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

    /*public void prepareExcel() {
        try {
            book = new XSSFWorkbook(new FileInputStream(mc.file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = book.getSheetAt(0);

        System.out.println("OOOOOOOOOOOOOOO");

        int i = START_ROW - 1;
        List<Integer> rowsForDel = new ArrayList<>();
        Row row;

        while (true) {
            i++;
            row = sheet.getRow(i);
            System.out.println(i);
            if (row.getCell(NAME_CELL) == null) {
                rowsForDel.add(i);
                System.out.println(i + " EEE");
                continue;
            }

            if ("ИТОГО".equals(row.getCell(NAME_CELL).getStringCellValue())) {
                break;
            }

        }
        sheet.getRow(i + 2).getCell(3).setCellValue("");
        sheet.getRow(i + 2).getCell(6).setCellValue("");


        rowsForDel.forEach(System.out::println);

        if (!rowsForDel.isEmpty()) {
            for (int j = 0; j < rowsForDel.size(); j++) {
                sheet.shiftRows(rowsForDel.get(j) + 1, i, -1);
                i--;
            }
        }

        try {
            FileOutputStream outFile = new FileOutputStream(mc.file);
            book.write(outFile);
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


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
}
