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
public class IngredientInsertController implements InsertController, Initializable {
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
    private TextField name;

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

        String nameIng = DBInit.getSubstring(" Название ингредиента=", "Название ингредиента=", item);
        String stockQuantity = DBInit.getSubstring(" Кол-во ингр-та на складе=", "Кол-во ингр-та на складе=", item);
        String criticalQuantity = DBInit.getSubstring(" Критическая норма=", "Критическая норма=", item);
        String priceValue = DBInit.getSubstring(" Цена ингр-та=", "Цена ингр-та=", item);

        System.out.println(item);

        name.setText(nameIng);
        stockField.setText(stockQuantity);
        criticalField.setText(criticalQuantity);
        price.setText(priceValue);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (stockField.getText().isEmpty() || criticalField.getText().isEmpty() || price.getText().isEmpty() || name.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String stockQuantity = stockField.getText();
            String criticalQuantity = criticalField.getText();
            String priceValue = price.getText();
            String nameValue = name.getText();


            if (insertMode == InsertMode.insert) {
                dbInit.insertIngredient(nameValue, Integer.valueOf(stockQuantity), Integer.valueOf(criticalQuantity), Integer.valueOf(priceValue));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateIngredient(id,nameValue, Integer.valueOf(stockQuantity), Integer.valueOf(criticalQuantity), Integer.valueOf(priceValue));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
