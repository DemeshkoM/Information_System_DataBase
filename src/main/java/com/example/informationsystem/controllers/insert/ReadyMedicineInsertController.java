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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class ReadyMedicineInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField stockField;
    @FXML
    private TextField criticalField;
    @FXML
    private TextField price;

    @FXML
    private ChoiceBox medTypeChoiceBox;
    @FXML
    private ChoiceBox medChoiceBox;


    private ObservableList<String> itemsMedType = FXCollections.<String>observableArrayList();
    private Map<String, Integer> MedType;

    private ObservableList<String> itemsMed = FXCollections.<String>observableArrayList();
    private Map<String, Integer> Med;


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

        medChoiceBox.setItems(itemsMed);
        try {
            ResultSet setMed = connection.executeQueryAndGetResult("select * from medicine");
            Med = new HashMap<>();
            itemsMed.clear();

            if (setMed != null) {
                while (setMed.next()) {
                    String nameType = setMed.getString(2);
                    Integer id = setMed.getInt(1);
                    Med.put(nameType, id);
                    itemsMed.add(nameType);
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

        String nameMedicament = DBInit.getSubstring(" name_medicament=", "name_medicament=", item);
        String nameMedType = DBInit.getSubstring(" name_med_type=", "name_med_type=", item);
        String stockQuantity = DBInit.getSubstring(" stock_quantity=", "stock_quantity=", item);
        String criticalQuantity = DBInit.getSubstring(" critical_quantity=", "critical_quantity=", item);
        String priceValue = DBInit.getSubstring(" price=", "price=", item);

        System.out.println(item);

        medChoiceBox.setValue(nameMedicament);
        medTypeChoiceBox.setValue(nameMedType);
        stockField.setText(stockQuantity);
        criticalField.setText(criticalQuantity);
        price.setText(priceValue);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (stockField.getText().isEmpty() || criticalField.getText().isEmpty() || price.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String stockQuantity = stockField.getText();
            String criticalQuantity = criticalField.getText();
            String priceValue = price.getText();

            String medType = medTypeChoiceBox.getValue().toString();
            int medTypeId = MedType.get(medType);

            String med = medChoiceBox.getValue().toString();
            int medId = Med.get(med);


            if (insertMode == InsertMode.insert) {
                dbInit.insertReadyMedicine(medId, medTypeId, Integer.valueOf(stockQuantity), Integer.valueOf(criticalQuantity), Integer.valueOf(priceValue));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateReadyMedicine(medId, medTypeId, Integer.valueOf(stockQuantity), Integer.valueOf(criticalQuantity), Integer.valueOf(priceValue));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
