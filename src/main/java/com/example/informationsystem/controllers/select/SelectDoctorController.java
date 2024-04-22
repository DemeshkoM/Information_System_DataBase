package com.example.informationsystem.controllers.select;

import com.example.informationsystem.controllers.base.CashierPatientInsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.*;

public class SelectDoctorController implements SelectController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");

    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button listButton;
    @FXML
    private ComboBox nameBox;

    private ObservableList<String> itemsName = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsPatient = new FilteredList<String>(itemsName);
    private Map<String, Integer> Name;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        nameBox.setItems(itemsName);

        try {
            ResultSet setName = connection.executeQueryAndGetResult("select id, full_name_doctor from doctor");
            Name = new HashMap<>();
            itemsName.clear();

            if (setName != null) {
                while (setName.next()) {
                    String namePatient = setName.getString(2);
                    Integer id = setName.getInt(1);
                    Name.put(namePatient, id);
                    itemsName.add(namePatient);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void listButtonTapped() {
        String name = "";

        if(!nameBox.getSelectionModel().isEmpty()) {
            name = nameBox.getValue().toString();
        }

        String nameField = "full_name_doctor";

        LinkedHashMap<String,String> sqlSelectMap = new LinkedHashMap();
        sqlSelectMap.put(nameField,name);

        String sql = "SELECT id, full_name_doctor";

        if(name.isEmpty()) {
            sql = "SELECT * FROM doctor";
        }
        else {
            sql += " FROM doctor WHERE ";

            for (Map.Entry<String,String> entry : sqlSelectMap.entrySet()) {
                if(!entry.getValue().isEmpty()) {
                    sql += entry.getKey() + "= '" + entry.getValue() + "' AND ";
                }
            }
            sql = sql.substring(0, sql.length() - 4);
        }



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
}