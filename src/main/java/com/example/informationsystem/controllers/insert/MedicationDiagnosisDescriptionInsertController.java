package com.example.informationsystem.controllers.insert;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
public class MedicationDiagnosisDescriptionInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField nameField;

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

    @Override
    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");
        String name = DBInit.getSubstring(" Область применения лек-ва=", "Область применения лек-ва=", item);
        nameField.setText(name);
    }

    public void insertButtonTapped() throws SQLException {
        if (nameField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");
        } else {
            String name = nameField.getText();

            if (insertMode == InsertMode.insert) {
                dbInit.insertMedicationDiagnosisDescription(name);
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateMedicationDiagnosisDescription(id, name);
            }
            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
