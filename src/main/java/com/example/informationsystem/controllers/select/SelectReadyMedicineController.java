package com.example.informationsystem.controllers.select;


import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SelectReadyMedicineController implements SelectController, Initializable {
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
    private ComboBox shortDescBox;
    @FXML
    private ComboBox typeOfMedBox;
    private ObservableList<String> itemsName = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsName = new FilteredList<String>(itemsName);
    private Map<String, Integer> Name;

    private ObservableList<String> itemsShortDesc = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsShortDesc = new FilteredList<String>(itemsShortDesc);
    private Map<String, Integer> ShortDesc;

    private ObservableList<String> itemsTypeOfMed = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsTypeOfMed = new FilteredList<String>(itemsTypeOfMed);
    private Map<String, Integer> TypeOfMed;

    private ObservableList<String> itemsOrderStatus = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsOrderStatus = new FilteredList<String>(itemsOrderStatus);
    private Map<String, Integer> OrderStatus;
    @FXML
    private TextField startPrice;
    @FXML
    private TextField endPrice;
    @FXML
    private ChoiceBox checkPrescBox;


    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        nameBox.setItems(itemsName);
        shortDescBox.setItems(itemsShortDesc);
        typeOfMedBox.setItems(itemsTypeOfMed);

        ObservableList<String> options = FXCollections.observableArrayList("Да",
                "Нет", "Не вести поиск по этому параметру");

        checkPrescBox.setItems(options);

        checkPrescBox.setValue("Не вести поиск по этому параметру");

        try {
            ResultSet setPatient = connection.executeQueryAndGetResult("select * from medicine");
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

            ResultSet setMedicine = connection.executeQueryAndGetResult("select * from medication_diagnosis_description");
            ShortDesc = new HashMap<>();
            itemsShortDesc.clear();

            if (setMedicine != null) {
                while (setMedicine.next()) {
                    String nameMedicine = setMedicine.getString(2);
                    Integer id = setMedicine.getInt(1);
                    ShortDesc.put(nameMedicine, id);
                    itemsShortDesc.add(nameMedicine);
                }
            }

            ResultSet setDoctor = connection.executeQueryAndGetResult("select * from medication_types");
            TypeOfMed = new HashMap<>();
            itemsTypeOfMed.clear();

            if (setDoctor != null) {
                while (setDoctor.next()) {
                    String nameDoctor = setDoctor.getString(2);
                    Integer id = setDoctor.getInt(1);
                    TypeOfMed.put(nameDoctor, id);
                    itemsTypeOfMed.add(nameDoctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnAllListButtonTapped() {
        String sql ="SELECT * FROM search_ready_medicine";

        name_obser = new SimpleStringProperty(sql);

        listener.changed(name_obser, "", sql);
        Stage stage = (Stage) listButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void listButtonTapped() {
        String name = "";
        String nameMedDiagnosisDescription = "";
        String typeOfMed = "";

        String startPriceValue = "";
        String endPriceValue = "";


        if(!nameBox.getSelectionModel().isEmpty()) {
            name = nameBox.getValue().toString();
        }

        String nameField = "name_medicament";

        if(!shortDescBox.getSelectionModel().isEmpty()) {
            nameMedDiagnosisDescription = shortDescBox.getValue().toString();
        }
        String nameMedDiagnosisDescriptionField = "name_med_diagnosis_description";

        if(!typeOfMedBox.getSelectionModel().isEmpty()) {
            typeOfMed = typeOfMedBox.getValue().toString();
        }
        String typeOfMedField = "name_med_type";

        if(!Objects.equals(startPrice.getText(), "")) {
            startPriceValue = startPrice.getText();
        }
        String startPriceField = "price";

        if(!Objects.equals(endPrice.getText(), "")) {
            endPriceValue = endPrice.getText();
        }


        LinkedHashMap<String,String> sqlSelectMap = new LinkedHashMap();
        sqlSelectMap.put(nameField,name);
        sqlSelectMap.put(nameMedDiagnosisDescriptionField,nameMedDiagnosisDescription);
        sqlSelectMap.put(typeOfMedField,typeOfMed);

        String sql = "SELECT * ";

        if(name.isEmpty() && nameMedDiagnosisDescription.isEmpty() && typeOfMed.isEmpty()) {
            sql = "SELECT * FROM search_ready_medicine";
        }
        else {
            sql += " FROM search_ready_medicine WHERE ";

            for (Map.Entry<String,String> entry : sqlSelectMap.entrySet()) {
                if(!entry.getValue().isEmpty()) {
                    sql += entry.getKey() + "= '" + entry.getValue() + "' AND ";
                }
            }

            if(checkPrescBox.getValue() == "Да") {
                sql += "selling_without_presc='продается' AND ";
            }
            else if(checkPrescBox.getValue() == "Нет") {
                sql += "selling_without_presc='не продается' AND ";
            }



            String partSelectDatePickers = makeSelectPrice(startPriceValue,endPriceValue);

            if (partSelectDatePickers.isEmpty()) {
                sql = sql.substring(0, sql.length() - 4);
            }
            else {
                sql += partSelectDatePickers;
            }
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

    public String makeSelectPrice(String startDateValue, String endDateValue) {
        String partOfSelect = "";

        if(!startDateValue.equals("") && !endDateValue.equals("")) {
            partOfSelect = "price BETWEEN '" + startDateValue + "' AND '" + endDateValue + "'";
        }

        return partOfSelect;
    }
}

