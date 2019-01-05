package ua.ats.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.entity.Product;
import ua.ats.util.InitParam;
import ua.ats.util.ParseData;
import ua.ats.view.MainController;

import java.math.BigDecimal;

@Component
public class Calculation {

    @Autowired
    private MainController mc;
    @Autowired
    private ParseData data;

    public void rewriteByPrice(String group, BigDecimal markup) {
        for (Product product : data.getProfile()) {
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
        mc.totalProfile = data.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void rewriteByWeight(String group, BigDecimal markup) {
        for (Product product : data.getProfile()) {
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
            mc.totalProfile = data.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void rewriteByCost(String group, BigDecimal markup) {
        for (Product product : data.getProfile()) {
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
        mc.totalProfile = data.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void rewriteProfile() {
        for (Product product : data.getProfile()) {

            product.setDiscount(mc.discountProfile);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountProfile)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteAccessories() {
        for (Product product : data.getAccessories()) {
            product.setDiscount(mc.discountAccessories);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountAccessories)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteSealant() {
        for (Product product : data.getSealant()) {
            product.setDiscount(mc.discountSealant);
            product.setSum((product.getCena().multiply(product.getQuantity())).multiply(mc.discountSealant)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void rewriteFurniture() {
        for (Product product : data.getFurniture()) {
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

        BigDecimal colProf = data.getProfile().stream().map(Product::getColorSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal colFurn = data.getFurniture().stream().map(Product::getColorSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        mc.totalColor = colProf.add(colFurn);

        mc.lblTotColor.setText(mc.totalColor.toString());
    }

    public void settingColorSum(int colorType) {
        for (Product product : data.getProfile()) {
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
            for (Product product : data.getFurniture()) {
                if (product.getColor() == 1) {
                    product.setColored(InitParam.colorFurn.divide(InitParam.rateUsd, 3, BigDecimal.ROUND_HALF_UP));
                    product.setColorSum((product.getQuantity().multiply(product.getColored()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
    }

    public void removeColorSum() {
        data.getProfile().forEach(j -> j.setColorSum(BigDecimal.ZERO));
        data.getFurniture().forEach(j -> j.setColorSum(BigDecimal.ZERO));
    }

    public void removeColorFromCena() {
        data.getProfile().forEach(p -> p.setSum(p.getSum().subtract(p.getColorSum())));
        data.getFurniture().forEach(p -> p.setSum(p.getSum().subtract(p.getColorSum())));
        mc.checkColorInCena = false;
    }

    public void addColorInCena() {
        data.getProfile().forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        data.getFurniture().forEach(p -> p.setSum(p.getSum().add(p.getColorSum())));
        mc.checkColorInCena = true;
    }
}
