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

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Calculation {

    // private static final Path DIR = Paths.get("e://alumotr");
    private final static int START_ROW = 11;
    private final static int NAME_CELL = 7;
    private final static int ARTICUL_CELL = 4;
    // private File file;

    private List<Product> profile = new ArrayList<>();
    private List<Product> accessories = new ArrayList<>();
    private List<Product> sealant = new ArrayList<>();
    private List<Product> furniture = new ArrayList<>();

    public XSSFWorkbook book;
    public XSSFSheet sheet;

    //private BigDecimal koef = new BigDecimal("120");

    private List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");
    public List<Integer> rowsForDel = new ArrayList<>();

    private StringBuilder noFind = new StringBuilder();
    public int lastRowNum;


    @Autowired
    private MainController mc;

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
                            profile.add(product);
                            initSumms(product);
                            break;
                        case "комплектующие":
                            accessories.add(product);
                            initSumms(product);
                            break;
                        case "уплотнители":
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

    public void rewriteByPrice(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                BigDecimal cost = product.getPrice().divide(new BigDecimal("1.2"), 3, BigDecimal.ROUND_HALF_UP);
                product.setCena(cost.multiply(markup).multiply(mc.discountProfile));
                product.setSum((product.getCena().multiply(product.getQuantity()).add(product.getColorSum()))
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
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
                product.setSum((product.getCena().multiply(product.getQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            mc.totalProfile = profile.stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void rewriteByCost(String group, BigDecimal markup) {
        for (Product product : profile) {
            if (group.equals(product.getGroupp().getName())) {
                product.setCena(product.getCost().multiply(markup).multiply(mc.discountProfile));
                product.setSum((product.getCena().multiply(product.getQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));
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
                switch (colorType) {
                    case 0:
                        product.setColorSum(BigDecimal.ZERO);
                        product.setColored(BigDecimal.ZERO);
                        break;
                    case 1:
                        product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                .setScale(2, BigDecimal.ROUND_HALF_UP));
                        product.setColored(mc.colored.multiply(product.getPerimeter()));
                        break;
                    case 2:
                        if (product.getBicolorWhiteIn() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                        } /*else {
                            product.setColorSum(BigDecimal.ZERO);
                        }*/
                        break;
                    case 3:
                        if (product.getBicolorWhiteOut() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                        } /*else {
                            product.setColorSum(BigDecimal.ZERO);
                        }*/
                        break;
                    case 4:
                        if (product.getBicolorWhiteOut() == 1 || product.getBicolorWhiteIn() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.colored.multiply(product.getPerimeter()));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                            product.setColored(mc.coloredBicolor.multiply(product.getPerimeter()));
                        } /*else {
                            product.setColorSum(BigDecimal.ZERO);
                        }*/
                        break;
                }
            }
        }

        if (colorType != 0) {
            for (Product product : furniture) {
                if (product.getColor() == 1) {
                    product.setColorSum((product.getQuantity().multiply(InitParam.colorFurn)
                            .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                    product.setColored(InitParam.colorFurn.divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP));
                }
            }
        } else {
            furniture.stream().filter(product -> product.getColor() == 1)
                    .forEach(product -> {
                        product.setColorSum(BigDecimal.ZERO);
                        product.setColored(BigDecimal.ZERO);
                    });
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
       /* for (Product product : profile) {
            if (product.getColor() == 1) {
                switch (colorType) {
                    case 0:
                        product.setColorSum(BigDecimal.ZERO);
                        break;
                    case 1:
                        product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                .setScale(2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 2:
                        if (product.getBicolorWhiteIn() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            product.setColorSum(BigDecimal.ZERO);
                        }
                        break;
                    case 3:
                        if (product.getBicolorWhiteOut() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            product.setColorSum(BigDecimal.ZERO);
                        }
                        break;
                    case 4:
                        if (product.getBicolorWhiteOut() == 1 || product.getBicolorWhiteIn() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.colored))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (product.getBicolor() == 1) {
                            product.setColorSum((product.getQuantity().multiply(product.getPerimeter().multiply(mc.coloredBicolor))
                                    .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            product.setColorSum(BigDecimal.ZERO);
                        }
                        break;
                }
            }
        }

        if (colorType != 0) {
            for (Product product : furniture) {
                if (product.getColor() == 1) {
                    product.setColorSum((product.getQuantity().multiply(InitParam.colorFurn)
                            .divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        } else {
            furniture.stream().filter(product -> product.getColor() == 1)
                    .forEach(product -> product.setColorSum(BigDecimal.ZERO));
        }
*/






        profile.forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        furniture.forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        mc.checkColorInCena = true;
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
}
