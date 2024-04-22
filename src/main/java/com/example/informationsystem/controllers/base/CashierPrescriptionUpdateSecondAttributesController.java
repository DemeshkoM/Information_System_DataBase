package com.example.informationsystem.controllers.base;
import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.InputFilter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class CashierPrescriptionUpdateSecondAttributesController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;

    @FXML
    private TextArea diagnosisField;
    @FXML
    private TextArea directionForUseField;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");

        String nameDiagnosis = DBInit.getSubstring(" diagnosis=", "diagnosis=", item);
        String nameDirection = DBInit.getSubstring(" direction_for_use=", "direction_for_use=", item);

        diagnosisField.setText(nameDiagnosis);
        directionForUseField.setText(nameDirection);
    }

    public void updateSecondAttributesPrescriptionTapped() {
        String diagnosis = diagnosisField.getText();
        String direction = directionForUseField.getText();

        int id = DBInit.getIdFrom(item);

        dbInit.updateSecondAttributesPrescription(id, diagnosis, direction);

        listener.changed(name_obser, "", name_obser);
        Stage stage = (Stage) insertButton.getScene().getWindow();
        stage.close();
    }
}
