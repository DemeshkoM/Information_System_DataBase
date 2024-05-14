package com.example.informationsystem.controllers.base.provider;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProviderRecipeListUpdateMedicineController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox medTypeChoiceBox;
    @FXML
    private ChoiceBox medDiagnosisDescriptionBox;

    @FXML
    private CheckBox sellingWithoutPrescCheckBox;

    private ObservableList<String> itemsMedType = FXCollections.<String>observableArrayList();
    private Map<String, Integer> MedType;
    private ObservableList<String> itemsMedDesc = FXCollections.<String>observableArrayList();
    private Map<String, Integer> MedDesc;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        medTypeChoiceBox.setItems(itemsMedType);
        try {
            ResultSet setMedType = connection.executeQueryAndGetResult("select * from medication_types");
            MedType = new HashMap<>();
            itemsMedType.clear();

            if (setMedType != null) {
                while (setMedType.next()) {
                    String nameType = setMedType.getString(2);
                    Integer id = setMedType.getInt(1);
                    MedType.put(nameType, id);
                    itemsMedType.add(nameType);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        medDiagnosisDescriptionBox.setItems(itemsMedDesc);
        try {
            ResultSet setMedDesc = connection.executeQueryAndGetResult("select * from medication_diagnosis_description");
            MedDesc = new HashMap<>();
            itemsMedDesc.clear();

            if (setMedDesc != null) {
                while (setMedDesc.next()) {
                    String nameType = setMedDesc.getString(2);
                    Integer id = setMedDesc.getInt(1);
                    MedDesc.put(nameType, id);
                    itemsMedDesc.add(nameType);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");
        String nameMedicament = DBInit.getSubstring(" Название лек-ва=", "Название лек-ва=", item);
        String nameMedType = DBInit.getSubstring(" Тип лек-ва=", "Тип лек-ва=", item);
        String nameMedDesc = DBInit.getSubstring(" Область применения лек-ва=", "Область применения лек-ва=", item);


        nameField.setText(nameMedicament);
        medTypeChoiceBox.setValue(nameMedType);
        medDiagnosisDescriptionBox.setValue(nameMedDesc);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (nameField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String name = nameField.getText();

            String medType = medTypeChoiceBox.getValue().toString();
            int medTypeId = MedType.get(medType);

            String medDesc = medDiagnosisDescriptionBox.getValue().toString();
            int medDescId = MedDesc.get(medDesc);

            if (insertMode == InsertMode.insert) {
                dbInit.insertMedicine(name, medTypeId, medDescId, "не продается");
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateMedicine(id, name, medTypeId, medDescId, "не продается");
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
