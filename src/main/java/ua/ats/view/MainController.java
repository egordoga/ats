package ua.ats.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;
import ua.ats.util.InitParam;
import ua.ats.util.ParseExelForDB;

import java.io.File;
import java.math.BigDecimal;


@Controller
public class MainController {

    private final static BigDecimal HUNDRED = new BigDecimal("100");

    public ToggleGroup color;
    public ToggleGroup w70markup;
    public ToggleGroup f50markup;
    public ToggleGroup l45markup;
    public ToggleGroup f50markupType;
    public ToggleGroup w70markupType;
    public ToggleGroup l45markupType;

    public Label totProf;
    public Label totAccess;
    public Label totSeal;
    public Label totFurnit;
    public Label totAll;
    public Label totColor;

    public BigDecimal markupF50 = new BigDecimal("1.2");
    public BigDecimal markupW70 = new BigDecimal("1.2");
    public BigDecimal markupL45 = new BigDecimal("1.2");
    public BigDecimal discountF50;
    public BigDecimal discountW70;
    public BigDecimal discountL45;
    public BigDecimal discountProfile = new BigDecimal("1");
    public BigDecimal discountAccessories = new BigDecimal("1");
    public BigDecimal discountSealant = new BigDecimal("1");
    public BigDecimal discountFurniture = new BigDecimal("1");

    public BigDecimal colored = BigDecimal.ZERO;
    public BigDecimal coloredBicolor = BigDecimal.ZERO;

    public BigDecimal cenaW70;
    public BigDecimal cenaF50;
    public BigDecimal cenaL45;
    public BigDecimal costW70;
    public BigDecimal costF50;
    public BigDecimal costL45;
    public BigDecimal cenaAccessories;
    public BigDecimal cenaSealant;
    public BigDecimal cenaFurniture;

    public BigDecimal totalAccessories;
    public BigDecimal totalSealant;
    public BigDecimal totalFurniture;
    public BigDecimal totalProfile;
    public BigDecimal totalAll;
    public BigDecimal totalColor;
    public CheckBox colorInCena;
    public TextField eur;
    public TextField usd;
    public Label cross;
    public TextField ralCena;
    public TextField ral9006Cena;
    public TextField ralBiOneCena;
    public TextField decCena;
    public TextField ralBi2Cena;
    public TextField ralBiWhiteCena;
    public TextField ralNumber;
    public TextField ralBiNumber;
    public TextField ralBi1Number;
    public TextField discProfile;
    public TextField discAccess;
    public TextField discSeal;
    public TextField discFurn;


    public String ralNum;
    public String ralBiNum;
    public String ralBi1Num;
    public TextField ralBiWhiteOneCena;

    private int costTypeF50 = 1;
    private int costTypeW70 = 1;
    private int costTypeL45 = 1;
    private int colorType = 0;        // 0 - none, 1 - color, bicolor: 2 - white in, 3 - white out, 4 - double

    public File file;

    @FXML
    private Label fileLbl;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Calculation calc;

    @Autowired
    private ParseExelForDB pe;

    @Autowired
    ProductRepository repository;

    @FXML
    private void initLbl() {
        openFile();
        InitParam.initParam();
        usd.setText(String.valueOf(InitParam.rateUsd));
        eur.setText(String.valueOf(InitParam.rateEur));
        cross.setText(String.valueOf(InitParam.crossRate));
        calc.fillLists(productRepository);
        fileLbl.setText("Файл: " + file.getName());
        countAndWriteTotal();
        listenMarkupF50();
        listenMarkupW70();
        colorListener();
        textFieldsInitAndListener();

    }

    @FXML
    private void listenMarkupF50() {

        f50markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "f5010":
                    markupF50 = new BigDecimal("1.1");
                    break;
                case "f5015":
                    markupF50 = new BigDecimal("1.15");
                    break;
                case "f5020":
                    markupF50 = new BigDecimal("1.2");
                    break;
                case "f5025":
                    markupF50 = new BigDecimal("1.25");
                    break;
                case "f5030":
                    markupF50 = new BigDecimal("1.3");
                    break;
            }

            switch (costTypeF50) {
                case 1:
                    calc.rewriteByPrice("F50", markupF50);
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal();
                    break;
            }
        });

    }

    @FXML
    private void listenMarkupW70() {

        w70markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "w7010":
                    markupW70 = new BigDecimal("1.1");
                    break;
                case "w7015":
                    markupW70 = new BigDecimal("1.15");
                    break;
                case "w7020":
                    markupW70 = new BigDecimal("1.2");
                    break;
                case "w7025":
                    markupW70 = new BigDecimal("1.25");
                    break;
                case "w7030":
                    markupW70 = new BigDecimal("1.3");
                    break;
            }

            switch (costTypeW70) {
                case 1:
                    calc.rewriteByPrice("W70", markupW70);
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteByWeight("W70", markupW70);
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal();
                    break;
            }
        });

    }

    @FXML
    private void listenMarkupL45() {

        l45markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "l4510":
                    markupL45 = new BigDecimal("1.1");
                    break;
                case "l4515":
                    markupL45 = new BigDecimal("1.15");
                    break;
                case "l4520":
                    markupL45 = new BigDecimal("1.2");
                    break;
                case "l4525":
                    markupL45 = new BigDecimal("1.25");
                    break;
                case "l4530":
                    markupL45 = new BigDecimal("1.3");
                    break;
            }

            switch (costTypeL45) {
                case 1:
                    calc.rewriteByPrice("L45", markupL45);
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal();
                    break;
            }
        });

    }

    @FXML
    private void listenTypeW70() {
        w70markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "w70price":
                    costTypeW70 = 1;
                    calc.rewriteByPrice("W70", markupW70);
                    countAndWriteTotal();
                    break;
               /* case "w70weight":
                    costTypeW70 = 2;
                    calc.rewriteByWeight("W70", markupW70);
                    countAndWriteTotal();
                    break;*/
                case "w70cost":
                    costTypeW70 = 3;
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal();
                    break;

            }
        });
    }

    @FXML
    private void listenTypeF50() {
        f50markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "f50price":
                    costTypeF50 = 1;
                    calc.rewriteByPrice("F50", markupF50);
                    countAndWriteTotal();
                    break;
                case "f50weight":
                    costTypeF50 = 2;
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal();
                    break;
                case "f50cost":
                    costTypeF50 = 3;
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal();
                    break;

            }
        });
    }

    @FXML
    private void listenTypeL45() {
        l45markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "l45price":
                    costTypeL45 = 1;
                    calc.rewriteByPrice("L45", markupL45);
                    countAndWriteTotal();
                    break;
                case "l45weight":
                    costTypeL45 = 2;
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal();
                    break;
                case "l45cost":
                    costTypeL45 = 3;
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal();
                    break;

            }
        });
    }



    private void colorListener() {
        color.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "noRal":
                    colored = BigDecimal.ZERO;
                    coloredBicolor = BigDecimal.ZERO;
                    //colorType = 0;
                    calc.removeColorSum();

                   /* calc.getProfile().forEach(System.out::println);
                    calc.getFurniture().forEach(System.out::println);*/

                    break;
                case "ral":
                    colored = InitParam.color;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    calc.settingColorSum(colorType);
                    /*calc.getProfile().forEach(System.out::println);
                    calc.getFurniture().forEach(System.out::println);*/

                    break;
                case "ral9006":
                    colored = InitParam.color9006;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    calc.settingColorSum(colorType);
                    break;
                case "biIn":
                    colored = InitParam.color;
                    coloredBicolor = InitParam.bicolorWithWhite;
                    colorType = 2;
                    calc.settingColorSum(colorType);
                    break;
                case "biOut":
                    colored = InitParam.color;
                    coloredBicolor = InitParam.bicolorWithWhite;
                    colorType = 3;
                    calc.settingColorSum(colorType);
                    break;
                case "dec":
                    colored = InitParam.dekor;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    calc.settingColorSum(colorType);
                    break;
            }
            //calc.settingColorSum(colorType);
            calc.rewriteColorTotal();
            //addColorInCena();
        });
    }

    @FXML
    private void addColorInCena() {
        if (colorInCena.isSelected()) {
            calc.settingColorSum(colorType);
            calc.addColorSum();
            countAndWriteTotal();
            totColor.setText("");
        } else {
            calc.removeColorSum();
            //calc.addColorSum();
            countAndWriteTotal();
            totColor.setText(totalColor.toString());
        }
    }


    private void textFieldsInitAndListener() {

        usd.setText(InitParam.rateUsd.toString());
        eur.setText(InitParam.rateEur.toString());
        ralCena.setText(InitParam.color.toString());
        ralBiOneCena.setText(InitParam.color.toString());
        ralBi2Cena.setText(InitParam.bicolor.toString());
        ral9006Cena.setText(InitParam.color9006.toString());
        ralBiWhiteCena.setText(InitParam.bicolorWithWhite.toString());
        ralBiWhiteOneCena.setText(InitParam.color.toString());
        decCena.setText(InitParam.dekor.toString());

        usd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.rateUsd = new BigDecimal(usd.getText());
                InitParam.initCross();
                cross.setText(InitParam.crossRate.toString());
            }
        });

        eur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.rateEur = new BigDecimal(eur.getText());
                InitParam.initCross();
                cross.setText(InitParam.crossRate.toString());
            }
        });

        ralNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ralNum = ralNumber.getText();
            }
        });

        ralCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.color = new BigDecimal(ralCena.getText());
            }
        });



        ral9006Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.color9006 = new BigDecimal(ral9006Cena.getText());
            }
        });

        ralBiWhiteCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.bicolor = new BigDecimal(ralBiWhiteCena.getText());
            }
        });

        ralBiWhiteOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.color = new BigDecimal(ralBiWhiteOneCena.getText());
            }
        });

        ralBiNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ralBiNum = ralBiNumber.getText();
            }
        });

        ralBi1Number.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ralBi1Num = ralBi1Number.getText();
            }
        });

        decCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.dekor = new BigDecimal(decCena.getText());
            }
        });

        ralBiOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.color = new BigDecimal(ralBiOneCena.getText());
            }
        });

        ralBi2Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.bicolor = new BigDecimal(ralBi2Cena.getText());
            }
        });

        discProfile.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if ("-".equals(discProfile.getText())) {
                    discountProfile = new BigDecimal(discProfile.getText()).add(HUNDRED)
                            .divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
                } else {
                    discountProfile = HUNDRED.subtract(new BigDecimal(discProfile.getText()))
                            .divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        });

    }



 /*   @FXML
    private File opFile(Stage primaryStage) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(primaryStage);
        System.out.println("FILE: " + file.getName());
        return file;
    }
*/

    private void countAndWriteTotal() {
        totalProfile = calc.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAccessories = calc.getAccessories().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSealant = calc.getSealant().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFurniture = calc.getFurniture().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
       totalAll = totalProfile.add(totalAccessories).add(totalSealant).add(totalFurniture);
        writeTotal();
    }

   /* private void initTotal() {
        totalProfile = calc.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAccessories = calc.getAccessories().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSealant = calc.getSealant().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFurniture = calc.getFurniture().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }*/

    private void writeTotal() {
        totProf.setText(totalProfile.toString());
        totAccess.setText(totalAccessories.toString());
        totSeal.setText(totalSealant.toString());
        totFurnit.setText(totalFurniture.toString());
        totAll.setText(totalAll.toString());
    }


    public void openFile() {
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(null);
       // pe.parseExel();
    }
}
