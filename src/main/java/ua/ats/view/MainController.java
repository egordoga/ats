package ua.ats.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;
import ua.ats.util.InitParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

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
    public TextField ralBiWhiteCena;
    public TextField decCena;
    public TextField ralBi2Cena;
    public TextField ralBiCena;
    public TextField ralNumber;


    public String ralNum;
    public TextField ralBiNumber;
    public TextField ralBi1Number;

    private List<Product> profileNew = new ArrayList<>();
    private List<Product> accessoriesNew = new ArrayList<>();
    private List<Product> sealantNew = new ArrayList<>();
    private List<Product> furnitureNew = new ArrayList<>();

    private int costTypeF50 = 1;
    private int costTypeW70 = 1;
    private int costTypeL45 = 1;
    private int colorType = 0;        // 0 - none, 1 - color, bicolor: 2 - white in, 3 - white out, 4 - double

    @FXML
    private Label fileLbl;

    //private Calculation calc = new Calculation();


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Calculation calc;

    @FXML
    private void initLbl() {
        InitParam.initParam();
        usd.setText(String.valueOf(InitParam.rateUsd));
        eur.setText(String.valueOf(InitParam.rateEur));
        cross.setText(String.valueOf(InitParam.crossRate));
        //calc.getStartRow();
        calc.fillLists(productRepository);
        fileLbl.setText("Файл: " + calc.getFile().getName());
        initTotal();
        countAndWriteTotal();
        //listenMarkupF50();
        //listenMarkupW70();
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
                    calc.rewriteF50ByPrice();
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteF505ByWeight();
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteF505ByCost();
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
                    calc.rewriteW70ByPrice();
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteW70ByWeight();
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteW705ByCost();
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
                    calc.rewriteL45ByPrice();
                    countAndWriteTotal();
                    break;
                case 2:
                    calc.rewriteL45ByWeight();
                    countAndWriteTotal();
                    break;
                case 3:
                    calc.rewriteL455ByCost();
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
                    calc.rewriteW70ByPrice();
                    countAndWriteTotal();
                    break;
                case "w70weight":
                    costTypeW70 = 2;
                    calc.rewriteW70ByWeight();
                    countAndWriteTotal();
                    break;
                case "w70cost":
                    costTypeW70 = 3;
                    calc.rewriteW705ByCost();
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
                    calc.rewriteF50ByPrice();
                    countAndWriteTotal();
                    break;
                case "f50weight":
                    costTypeF50 = 2;
                    calc.rewriteF505ByWeight();
                    countAndWriteTotal();
                    break;
                case "f50cost":
                    costTypeF50 = 3;
                    calc.rewriteF505ByCost();
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
                    calc.rewriteL45ByPrice();
                    countAndWriteTotal();
                    break;
                case "l45weight":
                    costTypeL45 = 2;
                    calc.rewriteL45ByWeight();
                    countAndWriteTotal();
                    break;
                case "l45cost":
                    costTypeL45 = 3;
                    calc.rewriteL455ByCost();
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
                    colorType = 0;
                    break;
                case "ral":
                    colored = InitParam.color;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    break;
                case "ral9006":
                    colored = InitParam.color9006;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    break;
                case "biIn":
                    colored = InitParam.color;
                    coloredBicolor = InitParam.bicolor;
                    colorType = 2;
                    break;
                case "biOut":
                    colored = InitParam.color;
                    coloredBicolor = InitParam.bicolor;
                    colorType = 3;
                    break;
                case "dec":
                    colored = InitParam.dekor;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 1;
                    break;
            }
            calc.settingColorSum(colorType);
            calc.rewriteColorTotal();
            addColorInCena();
        });
    }

    @FXML
    private void addColorInCena() {
        if (colorInCena.isSelected()) {
            calc.settingColorSum(colorType);
            calc.rewriteWithColor();
            initTotal();
            countAndWriteTotal();
            totColor.setText("");
        } else {
            calc.removeColorSum();
            calc.rewriteWithColor();
            initTotal();
            countAndWriteTotal();
            totColor.setText(totalColor.toString());
        }
    }


    private void textFieldsInitAndListener() {
        usd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.rateUsd = new BigDecimal(usd.getText());
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

        eur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.rateEur = new BigDecimal(eur.getText());
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

        eur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                InitParam.rateEur = new BigDecimal(eur.getText());
                InitParam.initCross();
                cross.setText(InitParam.crossRate.toString());
            }
        });

    }

    private void countAndWriteTotal() {
       totalAll = totalProfile.add(totalAccessories).add(totalSealant).add(totalFurniture);
        writeTotal();
    }

    private void initTotal() {
        totalProfile = calc.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAccessories = calc.getAccessories().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSealant = calc.getSealant().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFurniture = calc.getFurniture().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void writeTotal() {
        totProf.setText(totalProfile.toString());
        totAccess.setText(totalAccessories.toString());
        totSeal.setText(totalSealant.toString());
        totFurnit.setText(totalFurniture.toString());
        totAll.setText(totalAll.toString());
    }


}
