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
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.util.*;

public class SelectPatientController implements SelectController, Initializable {
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
    private ComboBox phoneBox;
    @FXML
    private ComboBox addressBox;

    private ObservableList<String> itemsName = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsPatient = new FilteredList<String>(itemsName);
    private Map<String, Integer> Name;

    private ObservableList<String> itemsPhone = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsMedicine = new FilteredList<String>(itemsPhone);
    private Map<String, Integer> Phone;

    private ObservableList<String> itemsAddress = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsDoctor = new FilteredList<String>(itemsAddress);
    private Map<String, Integer> Address;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        nameBox.getEditor().textProperty().addListener(new InputFilter(nameBox, filteredItemsPatient, false));
        phoneBox.getEditor().textProperty().addListener(new InputFilter(phoneBox, filteredItemsMedicine, false));
        addressBox.getEditor().textProperty().addListener(new InputFilter(addressBox, filteredItemsDoctor, false));

        nameBox.setItems(itemsName);
        phoneBox.setItems(itemsPhone);
        addressBox.setItems(itemsAddress);

        try {
            ResultSet setName = connection.executeQueryAndGetResult("select id, full_name_patient from patient");
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

            ResultSet setPhone = connection.executeQueryAndGetResult("select id, phone_number from patient");
            Phone = new HashMap<>();
            itemsPhone.clear();

            if (setPhone != null) {
                while (setPhone.next()) {
                    String namePhone = setPhone.getString(2);
                    Integer id = setPhone.getInt(1);
                    Phone.put(namePhone, id);
                    itemsPhone.add(namePhone);
                }
            }

            ResultSet setAddress = connection.executeQueryAndGetResult("select id, address from patient");
            Address = new HashMap<>();
            itemsAddress.clear();

            if (setAddress != null) {
                while (setAddress.next()) {
                    String nameAddress = setAddress.getString(2);
                    Integer id = setAddress.getInt(1);
                    Address.put(nameAddress, id);
                    itemsAddress.add(nameAddress);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void listButtonTapped() {
        String name = "";
        String phone = "";
        String address = "";

        if(!nameBox.getSelectionModel().isEmpty()) {
            name = nameBox.getValue().toString();
        }

        String nameField = "full_name_patient";

        if(!phoneBox.getSelectionModel().isEmpty()) {
            phone = phoneBox.getValue().toString();
        }
        String phoneField = "phone_number";

        if(!addressBox.getSelectionModel().isEmpty()) {
            address = addressBox.getValue().toString();
        }
        String addressField = "address";

        LinkedHashMap<String,String> sqlSelectMap = new LinkedHashMap();
        sqlSelectMap.put(nameField,name);
        sqlSelectMap.put(phoneField,phone);
        sqlSelectMap.put(addressField,address);

        String sql = "SELECT id, full_name_patient, phone_number, address, date_of_birth";

        if(name.isEmpty() && phone.isEmpty() && address.isEmpty()) {
            sql = "SELECT * FROM patient";
        }
        else {
            sql += " FROM patient WHERE ";

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
