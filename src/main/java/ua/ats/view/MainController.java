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
import ua.ats.util.WriteResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    /*public BigDecimal discountF50;
    public BigDecimal discountW70;
    public BigDecimal discountL45;*/
    public BigDecimal discountProfile = new BigDecimal("1");
    public BigDecimal discountAccessories = new BigDecimal("1");
    public BigDecimal discountSealant = new BigDecimal("1");
    public BigDecimal discountFurniture = new BigDecimal("1");

    public BigDecimal colored = BigDecimal.ZERO;
    public BigDecimal coloredBicolor = BigDecimal.ZERO;

    /*public BigDecimal cenaW70;
    public BigDecimal cenaF50;
    public BigDecimal cenaL45;
    public BigDecimal costW70;
    public BigDecimal costF50;
    public BigDecimal costL45;
    public BigDecimal cenaAccessories;
    public BigDecimal cenaSealant;
    public BigDecimal cenaFurniture;*/

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
    public TextField ralBiInNumber;
    public TextField ralBiOutNumber;
    public TextField ralBi1Number;
    public TextField discProfile;
    public TextField discAccess;
    public TextField discSeal;
    public TextField discFurn;


    public String ralNum;
    public String ralBiNum;
    public String ralBi1Num;
    public String strColor;

    public TextField ralBiWhiteOneCena;
    public RadioButton ral;
    public RadioButton ral9006;
    public RadioButton biIn;
    public RadioButton biOut;
    public RadioButton dec;
    public RadioButton bi2;
    public CheckBox withoutFurn;
    public CheckBox invoice;

    public int costTypeF50 = 1;
    public int costTypeW70 = 1;
    public int costTypeL45 = 1;
    public boolean checkColorInCena;
    public RadioButton f5020;
    public RadioButton w7020;
    public RadioButton l4520;
    public RadioButton f50price;
    public RadioButton w70price;
    public RadioButton l45price;
    public RadioButton noRal;
    private int colorType = 0;        // 0 - none, 1 - color, bicolor: 2 - white in, 3 - white out, 4 - double

    public File file;

    @FXML
    private Label fileLbl;

    public MainController() {
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Calculation calc;

    @Autowired
    private ParseExelForDB pe;

    @Autowired
    ProductRepository repository;

    @Autowired
    private WriteResult writeResult;

    @Autowired
    private InitParam ip;

    @FXML
    private void initLbl() {
        openFile();
        InitParam.initParam();
        usd.setText(String.valueOf(InitParam.rateUsd));
        eur.setText(String.valueOf(InitParam.rateEur));
        cross.setText(String.valueOf(InitParam.crossRate));
        calc.fillLists(productRepository);
        fileLbl.setText("Файл: " + file.getName());
        countAndWriteTotal(!withoutFurn.isSelected());
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
        f5020.setSelected(true);
        w7020.setSelected(true);
        l4520.setSelected(true);

        colored = BigDecimal.ZERO;
        coloredBicolor = BigDecimal.ZERO;
        colorType = 0;
        noRal.setSelected(true);


        discountProfile = new BigDecimal("1");
        discountAccessories = new BigDecimal("1");
        discountSealant = new BigDecimal("1");
        discountFurniture = new BigDecimal("1");
        discProfile.setText("");
        discAccess.setText("");
        discSeal.setText("");
        discFurn.setText("");


        costTypeF50 = 1;
        costTypeW70 = 1;
        costTypeL45 = 1;
        f50price.setSelected(true);
        w70price.setSelected(true);
        l45price.setSelected(true);

        checkColorInCena = false;
        colorInCena.setSelected(false);

        totalAccessories = BigDecimal.ZERO;
        totalSealant = BigDecimal.ZERO;
        totalFurniture = BigDecimal.ZERO;
        totalProfile = BigDecimal.ZERO;
        totalAll = BigDecimal.ZERO;
        totProf.setText("");
        totAccess.setText("");
        totSeal.setText("");
        totFurnit.setText("");
        totAll.setText("");
        totColor.setText("");

        withoutFurn.setSelected(false);
        invoice.setSelected(false);

        ralCena.setText("");
        ral9006Cena.setText("");
        ralBiOneCena.setText("");
        decCena.setText("");
        ralBi2Cena.setText("");
        ralBiWhiteCena.setText("");
        ralNumber.setText("");
        ralBiInNumber.setText("");
        ralBiOutNumber.setText("");
        ralBi1Number.setText("");

        calc.getProfile().clear();
        calc.getAccessories().clear();
        calc.getSealant().clear();
        calc.getFurniture().clear();
        calc.noFind.setLength(0);

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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("W70", markupW70);
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 2:
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case 3:
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
               /* case "w70weight":
                    costTypeW70 = 2;
                    calc.rewriteByWeight("W70", markupW70);
                    countAndWriteTotal();
                    break;*/
                case "w70cost":
                    costTypeW70 = 3;
                    calc.rewriteByCost("W70", markupW70);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case "f50weight":
                    costTypeF50 = 2;
                    calc.rewriteByWeight("F50", markupF50);
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case "f50cost":
                    costTypeF50 = 3;
                    calc.rewriteByCost("F50", markupF50);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case "l45weight":
                    costTypeL45 = 2;
                    calc.rewriteByWeight("L45", markupL45);
                    countAndWriteTotal(!withoutFurn.isSelected());
                    break;
                case "l45cost":
                    costTypeL45 = 3;
                    calc.rewriteByCost("L45", markupL45);
                    countAndWriteTotal(!withoutFurn.isSelected());
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
                    if (colorInCena.isSelected()) {
                        calc.removeColorFromCena();
                    }
                    calc.removeColorSum();
                    break;
                case "ral":
                    initColor(InitParam.color, BigDecimal.ZERO, 1);
                    break;
                case "ral9006":
                    initColor(InitParam.color9006, BigDecimal.ZERO, 1);
                    break;
                case "biIn":
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    break;
                case "biOut":
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    break;
                case "dec":
                    initColor(InitParam.dekor, BigDecimal.ZERO, 1);
                    break;
                case "bi2":
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    break;
            }
            calc.rewriteColorTotal();
        });
    }

    private void initColor(BigDecimal color, BigDecimal colorBi, int i) {
        colored = color;
        coloredBicolor = colorBi;
        colorType = i;
        addColorInCena();
        calc.rewriteColorTotal();
    }

    private void chkColorInCena() {
        colorInCena.selectedProperty().addListener((ov, t, t1) -> addColorInCena());
    }

    private void addColorInCena() {
        if (colorInCena.isSelected()) {
            if (checkColorInCena) {
                calc.removeColorFromCena();
            }
            calc.settingColorSum(colorType);
            calc.addColorInCena();
            countAndWriteTotal(!withoutFurn.isSelected());
        } else {
            if (checkColorInCena) {
                calc.removeColorFromCena();
            }
            calc.settingColorSum(colorType);
            countAndWriteTotal(!withoutFurn.isSelected());
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
                try {
                    InitParam.rateUsd = new BigDecimal(usd.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                InitParam.initCross();
                calc.rewriteFurniture();
                countAndWriteTotal(!withoutFurn.isSelected());
                usd.selectAll();
                cross.setText(InitParam.crossRate.toString());
            }
        });

        eur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.rateEur = new BigDecimal(eur.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                InitParam.initCross();
                calc.rewriteFurniture();
                countAndWriteTotal(!withoutFurn.isSelected());
                eur.selectAll();
                cross.setText(InitParam.crossRate.toString());
            }
        });

        ralNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                    ralNum = ralNumber.getText();
                ralNumber.selectAll();
            }
        });

        ralCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(ralCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (ral.isSelected()) {
                    initColor(InitParam.color, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                ralCena.selectAll();
            }
        });


        ral9006Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color9006 = new BigDecimal(ral9006Cena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (ral9006.isSelected()) {
                    initColor(InitParam.color9006, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                ral9006Cena.selectAll();
            }
        });

        ralBiWhiteOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(ralBiWhiteOneCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (biIn.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    calc.rewriteColorTotal();
                }

                if (biOut.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    calc.rewriteColorTotal();
                }
                ralBiWhiteOneCena.selectAll();
            }
        });

        ralBiWhiteCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.bicolorWithWhite = new BigDecimal(ralBiWhiteCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (biIn.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 2);
                    calc.rewriteColorTotal();
                }

                if (biOut.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolorWithWhite, 3);
                    calc.rewriteColorTotal();
                }
                ralBiWhiteCena.selectAll();
            }
        });


        ralBiInNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                //ralBiNum = ralBiInNumber.getText();
                ralBiInNumber.selectAll();
            }
        });

        ralBiOutNumber.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                //ralBiNum = ralBiInNumber.getText();
                ralBiInNumber.selectAll();
            }
        });

        ralBiOneCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.color = new BigDecimal(ralBiOneCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (bi2.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    calc.rewriteColorTotal();
                }
                ralBiOneCena.selectAll();
            }
        });

        ralBi2Cena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.bicolor = new BigDecimal(ralBi2Cena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (bi2.isSelected()) {
                    initColor(InitParam.color, InitParam.bicolor, 4);
                    calc.rewriteColorTotal();
                }
                ralBi2Cena.selectAll();
            }
        });

        decCena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    InitParam.dekor = new BigDecimal(decCena.getText());
                } catch (NumberFormatException e) {
                    alertNoNumber();
                }
                if (dec.isSelected()) {
                    initColor(InitParam.dekor, BigDecimal.ZERO, 1);
                    calc.rewriteColorTotal();
                }
                decCena.selectAll();
            }
        });


        discProfile.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountProfile = settingDiscount(discProfile);
                calc.rewriteProfile();
                countAndWriteTotal(!withoutFurn.isSelected());
            }
        });

        discAccess.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountAccessories = settingDiscount(discAccess);
                calc.rewriteAccessories();
                countAndWriteTotal(!withoutFurn.isSelected());
            }
        });

        discSeal.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountSealant = settingDiscount(discSeal);
                calc.rewriteSealant();
                countAndWriteTotal(!withoutFurn.isSelected());
            }
        });

        discFurn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                discountFurniture = settingDiscount(discFurn);
                calc.rewriteFurniture();
                countAndWriteTotal(!withoutFurn.isSelected());
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
        withoutFurn.selectedProperty().addListener((ov, t, t1) -> {
            countAndWriteTotal(!t1);
        });
    }


    private void countAndWriteTotal(boolean furn) {
        totalProfile = calc.getProfile().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAccessories = calc.getAccessories().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSealant = calc.getSealant().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFurniture = calc.getFurniture().stream().map(Product::getSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (furn) {
            totalAll = totalProfile.add(totalAccessories).add(totalSealant).add(totalFurniture);
        } else {
            totalAll = totalProfile.add(totalAccessories).add(totalSealant);
        }
        writeTotal();
    }


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

    public void showNoFind(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Не найдено в базе");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void alertNoNumber() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Не цифра");
        alert.setHeaderText(null);
        alert.setContentText("Введите цифровое выражение");
        alert.showAndWait();
    }

    @FXML
    private void saveExel() {
        if (file != null) {
            writeResult.writeExel(calc.getProfile());
            writeResult.writeExel(calc.getAccessories());
            writeResult.writeExel(calc.getSealant());
            writeResult.writeExel(calc.getFurniture());
            writeResult.decorateExel();
            writeResult.changeColorColumn();
            try {
                FileOutputStream outFile = new FileOutputStream(file);
                calc.book.write(outFile);
                calc.book.close();
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Запись файла");
            alert.setHeaderText(null);
            alert.setContentText("Файл уже записан.");
            alert.showAndWait();
        }
        file = null;
        rollBack();
    }


}
