package com.example.informationsystem.controllers.select;

import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.utils.InputFilter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SelectIngredientController implements SelectController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");

    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button listButton;
    @FXML
    private ComboBox nameBox;
    @FXML
    private TextField stockQuantityStartField;
    @FXML
    private TextField stockQuantityEndField;
    @FXML
    private TextField criticalQuantityStartField;
    @FXML
    private TextField criticalQuantityEndField;
    private ObservableList<String> itemsName = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsName = new FilteredList<String>(itemsName);
    private Map<String, Integer> Name;
    @FXML
    private TextField startPrice;
    @FXML
    private TextField endPrice;
    private Boolean textFieldEmpty;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        nameBox.getEditor().textProperty().addListener(new InputFilter(nameBox, filteredItemsName, false));

        nameBox.setItems(itemsName);

        textFieldEmpty = true;

        try {
            ResultSet setPatient = connection.executeQueryAndGetResult("select * from ingredient");
            Name = new HashMap<>();
            itemsName.clear();

            if (setPatient != null) {
                while (setPatient.next()) {
                    String namePatient = setPatient.getString(2);
                    Integer id = setPatient.getInt(1);
                    Name.put(namePatient, id);
                    itemsName.add(namePatient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnAllListButtonTapped() {
        String sql ="SELECT * FROM ingredient";

        name_obser = new SimpleStringProperty(sql);

        listener.changed(name_obser, "", sql);
        Stage stage = (Stage) listButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void listButtonTapped() {
        String name = "";

        String stockStartQuantity = "";
        String stockEndQuantity = "";

        String criticalStartQuantity = "";
        String criticalEndQuantity = "";

        String startPriceValue = "";
        String endPriceValue = "";


        if(!nameBox.getSelectionModel().isEmpty()) {
            name = nameBox.getValue().toString();
        }

        String nameField = "name_ingredient";

        if(!Objects.equals(stockQuantityStartField.getText(), "")) {
            textFieldEmpty = false;
            stockStartQuantity = stockQuantityStartField.getText();
        }

        if(!Objects.equals(stockQuantityEndField.getText(), "")) {
            textFieldEmpty = false;
            stockEndQuantity = stockQuantityEndField.getText();
        }

        if(!Objects.equals(criticalQuantityStartField.getText(), "")) {
            textFieldEmpty = false;
            criticalStartQuantity = criticalQuantityStartField.getText();
        }

        if(!Objects.equals(criticalQuantityEndField.getText(), "")) {
            textFieldEmpty = false;
            criticalEndQuantity = criticalQuantityEndField.getText();
        }


        if(!Objects.equals(startPrice.getText(), "")) {
            textFieldEmpty = false;
            startPriceValue = startPrice.getText();
        }

        if(!Objects.equals(endPrice.getText(), "")) {
            textFieldEmpty = false;
            endPriceValue = endPrice.getText();
        }

        String namePrice = "price";
        String nameStockQuantity = "stock_quantity";
        String nameCriticalQuantity = "critical_quantity";

        LinkedHashMap<String,String> sqlSelectMap = new LinkedHashMap();
        sqlSelectMap.put(nameField,name);

        String sql = "SELECT * ";

        if(name.isEmpty() && textFieldEmpty) {
            sql = "SELECT * FROM ingredient";
        }
        else {
            sql += " FROM ingredient WHERE ";

            for (Map.Entry<String,String> entry : sqlSelectMap.entrySet()) {
                if(!entry.getValue().isEmpty()) {
                    sql += entry.getKey() + "= '" + entry.getValue() + "' AND ";
                }
            }



            String partSelectStock = makeSelectPart(nameStockQuantity, stockStartQuantity,stockEndQuantity);

            String partSelectCritical = makeSelectPart(nameCriticalQuantity, criticalStartQuantity,criticalEndQuantity);

            String partSelectPrice = makeSelectPart(namePrice, startPriceValue,endPriceValue);

            LinkedHashMap<String,String> sqlSelectBetweenMap = new LinkedHashMap();
            sqlSelectBetweenMap.put(nameStockQuantity,partSelectStock);
            sqlSelectBetweenMap.put(nameCriticalQuantity,partSelectCritical);
            sqlSelectBetweenMap.put(namePrice,partSelectPrice);

            for (Map.Entry<String,String> entry : sqlSelectBetweenMap.entrySet()) {
                if(!entry.getValue().isEmpty()) {
                    sql += entry.getValue() + " AND ";
                }
            }

            sql = sql.substring(0, sql.length() - 4);
        }


        System.out.println(sql);
        name_obser = new SimpleStringProperty(sql);

        listener.changed(name_obser, "", sql);
        Stage stage = (Stage) listButton.getScene().getWindow();
        stage.close();
    }

    public ResultSet showResult(String sql) {
        ResultSet set = connection.executeQueryAndGetResult(sql);
        try {
            if (set != null) {
                System.out.println(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }

    public String makeSelectPart(String nameColumn, String startDateValue, String endDateValue) {
        String partOfSelect = "";

        if(!startDateValue.equals("") && !endDateValue.equals("")) {
            partOfSelect = nameColumn + " BETWEEN " + startDateValue + " AND " + endDateValue;
        }

        return partOfSelect;
    }
}
