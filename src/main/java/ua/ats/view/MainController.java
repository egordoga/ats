package ua.ats.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.ats.logic.Calculation;

@Component
public class MainController {

    @FXML
    private Label fileLbl;

    private Calculation calc = new Calculation();

    @FXML
    private void initLbl() {
        //calc.getStartRow();
        calc.fillLists();
        fileLbl.setText("Файл: " + calc.getFile().getName());
    }
}
