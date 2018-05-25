package ua.ats.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.AtsApplication;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainController {

    public ToggleGroup color;
    public ToggleGroup w70markup;
    public ToggleGroup f50markup;
    public ToggleGroup l45markup;
    private double markupF50 = 1.2;
    private double markupW70 = 1.2;
    private double markupL45 = 1.2;
    private double discountF50;
    private double discountW70;
    private double discountL45;
    private double discountProfile;
    private double discountAccessories;
    private double discountSealant;
    private double discountFurniture;

    private int cenaProfile;
    private int cenaAccessories;
    private int cenaSealant;
    private int cenaFurniture;


    private List<Product> profileNew = new ArrayList<>();
    private List<Product> accessoriesNew = new ArrayList<>();
    private List<Product> sealantNew = new ArrayList<>();
    private List<Product> furnitureNew = new ArrayList<>();

    @FXML
    private Label fileLbl;

    private Calculation calc = new Calculation();


    @Autowired
    private ProductRepository productRepository;

    @FXML
    private void initLbl() {
        //calc.getStartRow();
        //calc.fillLists(productRepository);
        //fileLbl.setText("Файл: " + calc.getFile().getName());
        f50markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
            System.out.println("Selected Radio Button - "+chk.getText() + "  ID= " + chk.getId());

        });

    }

    private void culcResult() {

        f50markup.selectedToggleProperty().addListener((ov, t, t1) -> {
            RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
            //System.out.println("Selected Radio Button - "+chk.getText() + "  ID= " + chk.getId());

            switch (chk.getId()) {
                case "f5010":
                    markupF50 = 1.1;
                    break;
            }
        });

    }

    private void fillProfile(double markup, double discount) {
        profileNew.clear();
    }
}
