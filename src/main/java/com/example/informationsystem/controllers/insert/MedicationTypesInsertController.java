package com.example.informationsystem.controllers.insert;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class MedicationTypesInsertController implements InsertController, Initializable {
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
    private ChoiceBox medCategoryChoiceBox;

    private ObservableList<String> itemsMedCategory = FXCollections.<String>observableArrayList();
    private Map<String, Integer> MedCategory;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        medCategoryChoiceBox.setItems(itemsMedCategory);
        try {
            ResultSet setMedCategory = connection.executeQueryAndGetResult("select * from medication_category");
            MedCategory = new HashMap<>();
            itemsMedCategory.clear();

            if (setMedCategory != null) {
                while (setMedCategory.next()) {
                    String nameCategory = setMedCategory.getString(2);
                    Integer id = setMedCategory.getInt(1);
                    MedCategory.put(nameCategory, id);
                    itemsMedCategory.add(nameCategory);
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
        String nameCategoryType = DBInit.getSubstring(" name_med_type=", "name_med_type=", item);
        String nameCategory = DBInit.getSubstring(" name_med_category_id=", "name_med_category_id=", item);
        System.out.println(item);

        nameField.setText(nameCategoryType);

        medCategoryChoiceBox.setValue(nameCategory);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (nameField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String name = nameField.getText();

            String medCategory = medCategoryChoiceBox.getValue().toString();
            int medCategoryId = MedCategory.get(medCategory);

            if (insertMode == InsertMode.insert) {
                dbInit.insertMedicationTypes(name, medCategoryId);
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateMedicationTypes(id, name, medCategoryId);
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
