package ua.ats.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;
import ua.ats.service.ProductService;
import ua.ats.util.InitParam;
import ua.ats.util.ParseData;
import ua.ats.util.ParseExcelForDB;
import ua.ats.util.WriteResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Controller
public class MainController {

    private final static BigDecimal HUNDRED = new BigDecimal("100");
    private final static File FILE_CHK = new File("C:\\Program Files\\Common Files\\Ats");

    public ToggleGroup tgColor;
    public ToggleGroup tgW70markup;
    public ToggleGroup tgF50markup;
    public ToggleGroup tgL45markup;
    public ToggleGroup tgF50markupType;
    public ToggleGroup tgW70markupType;
    public ToggleGroup tgL45markupType;

    public Label lblTotProf;
    public Label lblTotAccess;
    public Label lblCross;
    public Label lblTotSeal;
    public Label lblTotFurnit;
    public Label lblTotMat;
    public Label lblNoFurn;
    public Label lblTotAll;
    public Label lblTotColor;

    private BigDecimal markupF50 = new BigDecimal("1.2");
    private BigDecimal markupW70 = new BigDecimal("1.2");
    private BigDecimal markupL45 = new BigDecimal("1.2");

    public BigDecimal discountProfile = new BigDecimal("1");
    public BigDecimal discountAccessories = new BigDecimal("1");
    public BigDecimal discountSealant = new BigDecimal("1");
    public BigDecimal discountFurniture = new BigDecimal("1");

    public BigDecimal colored = BigDecimal.ZERO;
    public BigDecimal coloredBicolor = BigDecimal.ZERO;

    private BigDecimal totalAccessories;
    private BigDecimal totalSealant;
    private BigDecimal totalFurniture;
    private BigDecimal totalMat;
    private BigDecimal totalAll;
    public BigDecimal totalProfile;
    public BigDecimal totalColor;

    public TextField tfEur;
    public TextField tfUsd;
    public TextField tfRalCena;
    public TextField tfRal9006Cena;
    public TextField tfRalBiOneCena;
    public TextField tfDecCena;
    public TextField tfRalBi2Cena;
    public TextField tfRalBiWhiteCena;
    public TextField tfRalNumber;
    public TextField tfRalBiInNumber;
    public TextField tfRalBiOutNumber;
    public TextField tfRalBi1Number;
    public TextField tfRalBiWhiteOneCena;
    public TextField tfRalFactory;
    public TextField tfDiscProfile;
    public TextField tfDiscAccess;
    public TextField tfDiscSeal;
    public TextField tfDiscFurn;

    public String strRalNumber;

    public RadioButton rbRal;
    public RadioButton rbRal9006;
    public RadioButton rbBiIn;
    public RadioButton rbBiOut;
    public RadioButton rbDec;
    public RadioButton rbBi2;
    public RadioButton rbF5020;
    public RadioButton rbW7020;
    public RadioButton rbL4520;
    public RadioButton rbF50price;
    public RadioButton rbW70price;
    public RadioButton rbL45price;
    public RadioButton rbNoRal;
    public RadioButton rbRalFactory;

    public CheckBox cbColorInCena;
    public CheckBox cbWithoutFurn;
    public CheckBox cbInvoice;

    private int colorType = 0;        // 0 - none, 1 - tgColor, bicolor: 2 - white in, 3 - white out,
    // 4 - double, 5 - coloredInFactory
    private int costTypeF50 = 1;
    private int costTypeW70 = 1;
    private int costTypeL45 = 1;
    public boolean checkColorInCena;

    public File file;

    @FXML
    private Label fileLbl;


    @Autowired
    private Calculation calc;
    @Autowired
    private ProductService productService;
    @Autowired
    private WriteResult writeResult;
    @Autowired
    private ParseExcelForDB parseExcelForDB;
    @Autowired
    private ParseData data;

    @FXML
    private void initLbl() {
        if (!FILE_CHK.exists()) {
            alertNoLicense();
            return;
        }
        openFile();
        InitParam.initParam();
        tfUsd.setText(String.valueOf(InitParam.rateUsd));
        tfEur.setText(String.valueOf(InitParam.rateEur));
        lblCross.setText(String.valueOf(InitParam.crossRate));
        data.fillLists(productService);
        fileLbl.setText("Файл: " + file.getName());
        countAndWriteTotal(!cbWithoutFurn.isSelected());
        listenMarkupF50();
        listenMarkupW70();
        listenMarkupL45();
        listenTypeF50();
        listenTypeW70();
        listenTypeL45();
        colorListener();
        chkColorInCena();
        chkNeedFurnListener();
        textFieldsInitAndListener();

    }

    private void rollBack() {

        markupF50 = new BigDecimal("1.2");
        markupW70 = new BigDecimal("1.2");
        markupL45 = new BigDecimal("1.2");
        rbF5020.setSelected(true);
        rbW7020.setSelected(true);
        rbL4520.setSelected(true);

        colored = BigDecimal.ZERO;
        coloredBicolor = BigDecimal.ZERO;
        colorType = 0;
        rbNoRal.setSelected(true);

        discountProfile = new BigDecimal("1");
        discountAccessories = new BigDecimal("1");
        discountSealant = new BigDecimal("1");
        discountFurniture = new BigDecimal("1");
        tfDiscProfile.setText("");
        tfDiscAccess.setText("");
        tfDiscSeal.setText("");
        tfDiscFurn.setText("");

        costTypeF50 = 1;
        costTypeW70 = 1;
        costTypeL45 = 1;
        rbF50price.setSelected(true);
        rbW70price.setSelected(true);
        rbL45price.setSelected(true);

        checkColorInCena = false;
        cbColorInCena.setSelected(false);

        totalAccessories = BigDecimal.ZERO;
        totalSealant = BigDecimal.ZERO;
        totalFurniture = BigDecimal.ZERO;
        totalProfile = BigDecimal.ZERO;
        totalMat = BigDecimal.ZERO;
        totalAll = BigDecimal.ZERO;
        lblTotProf.setText("");
        lblTotAccess.setText("");
        lblTotSeal.setText("");
        lblTotFurnit.setText("");
        lblTotMat.setText("");
        lblTotAll.setText("");
        lblTotColor.setText("");

        cbWithoutFurn.setSelected(false);
        cbInvoice.setSelected(false);

        tfRalCena.setText("");
        tfRal9006Cena.setText("");
        tfRalFactory.setText("");
        tfRalBiOneCena.setText("");
        tfRalBiWhiteOneCena.setText("");
        tfDecCena.setText("");
        tfRalBi2Cena.setText("");
        tfRalBiWhiteCena.setText("");
        tfRalNumber.setText("");
        tfRalBiInNumber.setText("");
        tfRalBiOutNumber.setText("");
        tfRalBi1Number.setText("");

        data.getProfile().clear();
        data.getAccessories().clear();
        data.getSealant().clear();
        data.getFurniture().clear();
        data.getMatInstall().clear();
        data.getNoFind().setLength(0);


    }

    @FXML
    private void listenMarkupF50() {

        tgF50markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "f5010":
                    markupF50 = new BigDecimal("1.1");
                    break;
                case "f5015":
                    markupF50 = new BigDecimal("1.15");
                    break;
                case "rbF5020":
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
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
            }
        });

    }

    @FXML
    private void listenMarkupW70() {

        tgW70markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "w7010":
                    markupW70 = new BigDecimal("1.1");
                    break;
                case "w7015":
                    markupW70 = new BigDecimal("1.15");
                    break;
                case "rbW7020":
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
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("W70", markupW70);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
            }
        });

    }

    @FXML
    private void listenMarkupL45() {

        tgL45markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "l4510":
                    markupL45 = new BigDecimal("1.1");
                    break;
                case "l4515":
                    markupL45 = new BigDecimal("1.15");
                    break;
                case "rbL4520":
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
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
            }
        });

    }

    @FXML
    private void listenTypeW70() {
        tgW70markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "rbW70price":
                    costTypeW70 = 1;
                    calc.rewriteByPrice("W70", markupW70);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case "w70cost":
                    costTypeW70 = 3;
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;

            }
        });
    }

    @FXML
    private void listenTypeF50() {
        tgF50markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "rbF50price":
                    costTypeF50 = 1;
                    calc.rewriteByPrice("F50", markupF50);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case "f50weight":
                    costTypeF50 = 2;
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case "f50cost":
                    costTypeF50 = 3;
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;

            }
        });
    }

    @FXML
    private void listenTypeL45() {
        tgL45markupType.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();
            switch (chk.getId()) {
                case "rbL45price":
                    costTypeL45 = 1;
                    calc.rewriteByPrice("L45", markupL45);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case "l45weight":
                    costTypeL45 = 2;
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;
                case "l45cost":
                    costTypeL45 = 3;
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal(!cbWithoutFurn.isSelected());
                    break;

            }
        });
    }

    private void colorListener() {
        tgColor.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton) t1.getToggleGroup().getSelectedToggle();

            switch (chk.getId()) {
                case "rbNoRal":
                    colored = BigDecimal.ZERO;
                    coloredBicolor = BigDecimal.ZERO;
                    colorType = 0;
                    if (cbColorInCena.isSelected()) {
                        calc.removeColorFromCena();
                    }
                    calc.removeColorSum();
                    strRalNumber = "RAL9016";
                    break;
                case "rbRal":
                    initColor(InitParam.color, BigDecimal.ZERO, 1);
                    if (tfRalNumber.getText() != null && !"".equals(tfRalNumber.getText())) {
                        strRalNumber = "RAL" + tfRalNumber.getText();
                    }
                    break;
                case "rbRal9006":
                    initColor(InitParam.color9006, BigDecimal.ZERO, 1);
                    strRalNumber = "RAL9006";
                    break;
                case "rbBiIn":
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    if (tfRalBi1Number.getText() != null && !"".equals(tfRalBi1Number.getText())) {
                        strRalNumber = "RAL" + tfRalBi1Number.getText() + "/9016";
                    }
                    break;
                case "rbBiOut":
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    if (tfRalBi1Number.getText() != null && !"".equals(tfRalBi1Number.getText())) {
                        strRalNumber = "RAL9016/" + tfRalBi1Number.getText();
                    }
                    break;
                case "rbDec":
                    initColor(InitParam.dekor, BigDecimal.ZERO, 1);
                    strRalNumber = "DECOR";
                    break;
                case "rbBi2":
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    if (tfRalBiInNumber.getText() != null && tfRalBiOutNumber.getText() != null
                            && !"".equals(tfRalBiInNumber.getText()) && !"".equals(tfRalBiOutNumber.getText())) {
                        strRalNumber = "RAL" + tfRalBiOutNumber.getText() + "/" + tfRalBiInNumber.getText();
                    }
                    break;
                case "rbRalFactory":
                    initColor(BigDecimal.ZERO, BigDecimal.ZERO, 5);
                    if (tfRalNumber.getText() != null && !"".equals(tfRalNumber.getText())) {
                        strRalNumber = "RAL" + tfRalFactory.getText();
                    }
            }
            calc.rewriteColorTotal();
        });
    }

    private void initColor(BigDecimal color, BigDecimal colorBi, int i) {
        colored = color.divide(InitParam.rateUsd, 4, BigDecimal.ROUND_HALF_UP);
        coloredBicolor = colorBi.divide(InitParam.rateUsd, 2, BigDecimal.ROUND_HALF_UP);
        colorType = i;
        addColorInCena();
        calc.rewriteColorTotal();
    }

    private void chkColorInCena() {
        cbColorInCena.selectedProperty().addListener((ov, t, t1) -> addColorInCena());
    }

    private void addColorInCena() {
        if (cbColorInCena.isSelected()) {
            if (checkColorInCena) {
                calc.removeColorFromCena();
            }
            calc.settingColorSum(colorType);
            calc.addColorInCena();
            countAndWriteTotal(!cbWithoutFurn.isSelected());
        } else {
            if (checkColorInCena) {
                calc.removeColorFromCena();
            }
            calc.settingColorSum(colorType);
            countAndWriteTotal(!cbWithoutFurn.isSelected());
        }
    }

    private void textFieldsInitAndListener() {

        tfUsd.setText(InitParam.rateUsd.toString());
        tfEur.setText(InitParam.rateEur.toString());
        tfRalCena.setText(InitParam.color.toString());
        tfRalBiOneCena.setText(InitParam.color.toString());
        tfRalBi2Cena.setText(InitParam.bicolor.toString());
        tfRal9006Cena.setText(InitParam.color9006.toString());
        tfRalBiWhiteCena.setText(InitParam.bicolorWithWhite.toString());
        tfRalBiWhiteOneCena.setText(InitParam.color.toString());
        tfDecCena.setText(InitParam.dekor.toString());

        tfUsd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.rateUsd = new BigDecimal(tfUsd.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                InitParam.initCross();
                calc.rewriteFurniture();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
                tfUsd.selectAll();
                lblCross.setText(InitParam.crossRate.toString());
            }
        });

        tfEur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.rateEur = new BigDecimal(tfEur.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                InitParam.initCross();
                calc.rewriteFurniture();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
                tfEur.selectAll();
                lblCross.setText(InitParam.crossRate.toString());
            }
        });

        tfRalNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                strRalNumber = "RAL" + tfRalNumber.getText();
                tfRalNumber.selectAll();
            }
        });

        tfRalCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(tfRalCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbRal.isSelected()) {
                    initColor(InitParam.color, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                tfRalCena.selectAll();
            }
        });


        tfRal9006Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color9006 = new BigDecimal(tfRal9006Cena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbRal9006.isSelected()) {
                    initColor(InitParam.color9006, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                tfRal9006Cena.selectAll();
                strRalNumber = "RAL9006";
            }
        });

        tfRalBiWhiteOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(tfRalBiWhiteOneCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbBiIn.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    calc.rewriteColorTotal();
                }

                if (rbBiOut.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    calc.rewriteColorTotal();
                }
                tfRalBiWhiteOneCena.selectAll();
            }
        });

        tfRalBiWhiteCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.bicolorWithWhite = new BigDecimal(tfRalBiWhiteCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbBiIn.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    calc.rewriteColorTotal();
                }

                if (rbBiOut.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    calc.rewriteColorTotal();
                }
                tfRalBiWhiteCena.selectAll();
            }
        });


        tfRalBi1Number.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (rbBiOut.isSelected()) {
                    strRalNumber = "RAL9016/" + tfRalBi1Number.getText();
                }
                if (rbBiIn.isSelected()) {
                    strRalNumber = "RAL" + tfRalBi1Number.getText() + "/9016";
                }
                tfRalBi1Number.selectAll();
            }
        });

        tfRalBiInNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                strRalNumber = "RAL" + tfRalBiOutNumber.getText() + "/" + tfRalBiInNumber.getText();
                tfRalBiInNumber.selectAll();
            }
        });

        tfRalBiOutNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                strRalNumber = "RAL" + tfRalBiOutNumber.getText() + "/" + tfRalBiInNumber.getText();
                tfRalBiInNumber.selectAll();
            }
        });

        tfRalBiOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(tfRalBiOneCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbBi2.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    calc.rewriteColorTotal();
                }
                tfRalBiOneCena.selectAll();
            }
        });

        tfRalBi2Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.bicolor = new BigDecimal(tfRalBi2Cena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbBi2.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    calc.rewriteColorTotal();
                }
                tfRalBi2Cena.selectAll();
            }
        });

        tfDecCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.dekor = new BigDecimal(tfDecCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (rbDec.isSelected()) {
                    initColor(InitParam.dekor, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                tfDecCena.selectAll();
            }
        });

        tfRalFactory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                strRalNumber = "RAL" + tfRalFactory.getText();
                tfRalFactory.selectAll();
            }
        });


        tfDiscProfile.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountProfile = settingDiscount(tfDiscProfile);
                calc.rewriteProfile();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
            }
        });

        tfDiscAccess.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountAccessories = settingDiscount(tfDiscAccess);
                calc.rewriteAccessories();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
            }
        });

        tfDiscSeal.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountSealant = settingDiscount(tfDiscSeal);
                calc.rewriteSealant();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
            }
        });

        tfDiscFurn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountFurniture = settingDiscount(tfDiscFurn);
                calc.rewriteFurniture();
                countAndWriteTotal(!cbWithoutFurn.isSelected());
            }
        });
    }

    private BigDecimal settingDiscount(TextField textField) {
        BigDecimal discount = BigDecimal.ONE;
        if ("".equals(textField.getText()) || "0".equals(textField.getText())) {
            return discount;
        } else {
            try {
                if ('-' == (textField.getText().charAt(0))) {
                    discount = (HUNDRED.add(new BigDecimal(textField.getText().substring(1))))
                            .divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
                } else {
                    discount = HUNDRED.subtract(new BigDecimal(textField.getText()))
                            .divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
                }
            } catch (NumberFormatException e) {
                alertNoNumber();
            }
        }
        textField.selectAll();
        return discount;
    }

    private void chkNeedFurnListener() {
        cbWithoutFurn.selectedProperty().addListener((ov, t, t1) -> countAndWriteTotal(!t1));
    }

    private void countAndWriteTotal(boolean furn) {
        totalProfile = data.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAccessories = data.getAccessories().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSealant = data.getSealant().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFurniture = data.getFurniture().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalMat = data.getMatInstall().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (furn) {
            totalAll = totalProfile.add(totalAccessories).add(totalSealant).add(totalFurniture).add(totalMat);
        } else {
            totalAll = totalProfile.add(totalAccessories).add(totalSealant).add(totalMat);
        }
        writeTotal();
    }


    private void writeTotal() {
        lblTotProf.setText(totalProfile.toString());
        lblTotAccess.setText(totalAccessories.toString());
        lblTotSeal.setText(totalSealant.toString());
        lblTotFurnit.setText(totalFurniture.toString());
        lblTotMat.setText(totalMat.toString());
        lblTotAll.setText(totalAll.toString());
        if (cbWithoutFurn.isSelected()) {
            lblNoFurn.setText("/В общей сумме не учитывается/");
        } else {
            lblNoFurn.setText("");
        }
    }


    private void openFile() {
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(null);
    }

    public void showNoFind(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Не найдено в базе");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void alertNoNumber() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Не цифра");
        alert.setHeaderText(null);
        alert.setContentText("Введите цифровое выражение");
        alert.showAndWait();
    }

    private void alertNoLicense() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Не твоё");
        alert.setHeaderText(null);
        alert.setContentText("Паленная версия");
        alert.showAndWait();
    }

    @FXML
    private void saveExcel() {
        if (file != null) {
            writeResult.writeExcel(data.getProfile());
            writeResult.writeExcel(data.getAccessories());
            writeResult.writeExcel(data.getSealant());
            if (data.getFurniture().size() > 0) {
                if (!cbWithoutFurn.isSelected()) {
                    writeResult.writeExcel(data.getFurniture());
                } else {
                    writeResult.writeExcelFurnZero();
                }
            }

            writeResult.writeExcelAllSum();
            writeResult.writeQuantitySealent();
            writeResult.decorateExcel();
            writeResult.changeColorColumn();
            try {
                FileOutputStream outFile = new FileOutputStream(file);
                data.getBook().write(outFile);
                data.getBook().close();
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Запись файла");
            alert.setHeaderText(null);
            alert.setContentText("Файл не открыт или уже записан.");
            alert.showAndWait();
        }
        file = null;
        rollBack();
    }

    @FXML
    public void downloadDB() {
        parseExcelForDB.parseExcel();
    }


}
