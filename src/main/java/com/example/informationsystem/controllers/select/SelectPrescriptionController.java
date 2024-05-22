package com.example.informationsystem.controllers.select;

import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.utils.DatePickerFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SelectPrescriptionController implements SelectController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");

    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button listButton;
    @FXML
    private ComboBox patientBox;
    @FXML
    private ComboBox medicineBox;
    @FXML
    private ComboBox doctorBox;
    @FXML
    private ComboBox orderStatusBox;

    private ObservableList<String> itemsPatient = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsPatient = new FilteredList<String>(itemsPatient);
    private Map<String, Integer> Patient;

    private ObservableList<String> itemsMedicine = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsMedicine = new FilteredList<String>(itemsMedicine);
    private Map<String, Integer> Medicine;

    private ObservableList<String> itemsDoctor = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsDoctor = new FilteredList<String>(itemsDoctor);
    private Map<String, Integer> Doctor;

    private ObservableList<String> itemsOrderStatus = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsOrderStatus = new FilteredList<String>(itemsOrderStatus);
    private Map<String, Integer> OrderStatus;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private DatePicker salesDate;
    @FXML
    private DatePicker salesEndPredDate;
    @FXML
    private ChoiceBox modeDateSelectBox;
    private DatePickerFormatter datePickerFormatter;


    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        datePickerFormatter = new DatePickerFormatter();

        datePickerFormatter.setDatePickerFormatter(startDate);
        datePickerFormatter.setDatePickerFormatter(endDate);
        datePickerFormatter.setDatePickerFormatter(salesDate);
        datePickerFormatter.setDatePickerFormatter(salesEndPredDate);

        patientBox.getEditor().textProperty().addListener(new InputFilter(patientBox, filteredItemsPatient, false));
        medicineBox.getEditor().textProperty().addListener(new InputFilter(medicineBox, filteredItemsMedicine, false));
        doctorBox.getEditor().textProperty().addListener(new InputFilter(doctorBox, filteredItemsDoctor, false));
        orderStatusBox.getEditor().textProperty().addListener(new InputFilter(orderStatusBox, filteredItemsOrderStatus, false));

        patientBox.setItems(itemsPatient);
        medicineBox.setItems(itemsMedicine);
        doctorBox.setItems(itemsDoctor);
        orderStatusBox.setItems(itemsOrderStatus);

        ObservableList<String> options = FXCollections.observableArrayList("Отмена поиска по временным интервалам",
                "Интервал м/у датой открытия и датой закрытия заказа",
                "Интервал м/у датой открытия и датой оплаты заказа",
                "Интервал м/у датой оплаты и датой закрытия заказа",
                "Интервал в котором произошла оплата");

        modeDateSelectBox.setItems(options);

        modeDateSelectBox.setValue("Отмена поиска по временным интервалам");

        salesDate.setDisable(true);
        salesEndPredDate.setDisable(true);
        startDate.setDisable(true);
        endDate.setDisable(true);


        modeDateSelectBox.setOnAction(event -> {
            checkEditableDatePickers();
        });

        try {
            ResultSet setPatient = connection.executeQueryAndGetResult("select * from patient");
            Patient = new HashMap<>();
            itemsPatient.clear();

            if (setPatient != null) {
                while (setPatient.next()) {
                    String namePatient = setPatient.getString(2);
                    Integer id = setPatient.getInt(1);
                    Patient.put(namePatient, id);
                    itemsPatient.add(namePatient);
                }
            }

            ResultSet setMedicine = connection.executeQueryAndGetResult("select * from medicine");
            Medicine = new HashMap<>();
            itemsMedicine.clear();

            if (setMedicine != null) {
                while (setMedicine.next()) {
                    String nameMedicine = setMedicine.getString(2);
                    Integer id = setMedicine.getInt(1);
                    Medicine.put(nameMedicine, id);
                    itemsMedicine.add(nameMedicine);
                }
            }

            ResultSet setDoctor = connection.executeQueryAndGetResult("select * from doctor");
            Doctor = new HashMap<>();
            itemsDoctor.clear();

            if (setDoctor != null) {
                while (setDoctor.next()) {
                    String nameDoctor = setDoctor.getString(2);
                    Integer id = setDoctor.getInt(1);
                    Doctor.put(nameDoctor, id);
                    itemsDoctor.add(nameDoctor);
                }
            }

            ResultSet setOrderStatus = connection.executeQueryAndGetResult("select * from order_status");
            OrderStatus = new HashMap<>();
            itemsOrderStatus.clear();

            if (setOrderStatus != null) {
                while (setOrderStatus.next()) {
                    String nameOrderStatus = setOrderStatus.getString(2);
                    Integer id = setOrderStatus.getInt(1);
                    OrderStatus.put(nameOrderStatus, id);
                    itemsOrderStatus.add(nameOrderStatus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void listButtonTapped() {
        String patient = "";
        String medicine = "";
        String doctor = "";
        String orderStatus = "";

        String startDateValue = "";
        String endDateValue = "";
        String salesDateValue = "";
        String salesEndPredDateValue = "";


        if(!patientBox.getSelectionModel().isEmpty()) {
            patient = patientBox.getValue().toString();
        }

        String patientField = "full_name_patient";

        if(!medicineBox.getSelectionModel().isEmpty()) {
            medicine = medicineBox.getValue().toString();
        }
        String medicineField = "name_medicament";

        if(!doctorBox.getSelectionModel().isEmpty()) {
            doctor = doctorBox.getValue().toString();
        }
        String doctorField = "full_name_doctor";

        if(!orderStatusBox.getSelectionModel().isEmpty()) {
            orderStatus = orderStatusBox.getValue().toString();
        }
        String orderStatusField = "name_order_status";

        if(!startDate.isDisabled()) {
            startDateValue = startDate.getValue().toString();
        }
        String startDateField = "start_date";

        if(!endDate.isDisabled()) {
            endDateValue = endDate.getValue().toString();
        }
        String endDateField = "end_date";

        if(!salesDate.isDisabled()) {
            salesDateValue = salesDate.getValue().toString();
        }
        String salesDateField = "sales_date";

        if(!salesEndPredDate.isDisabled()) {
            salesEndPredDateValue = salesEndPredDate.getValue().toString();
        }

        LinkedHashMap<String,String> sqlSelectMap = new LinkedHashMap();
        sqlSelectMap.put(patientField,patient);
        sqlSelectMap.put(medicineField,medicine);
        sqlSelectMap.put(doctorField,doctor);
        sqlSelectMap.put(orderStatusField,orderStatus);

        String sql = "SELECT id, full_name_patient, name_medicament, name_med_diagnosis_description, full_name_doctor, amount, diagnosis, direction_for_use," +
                "start_date, end_date, sales_date, name_order_status";

        if(patient.isEmpty() && medicine.isEmpty() && doctor.isEmpty() && orderStatus.isEmpty() &&
                Objects.equals(modeDateSelectBox.getValue().toString(), "Отмена поиска по временным интервалам")) {
            sql = "SELECT * FROM search_prescription";
        }
        else {
            sql += " FROM search_prescription WHERE ";

            for (Map.Entry<String,String> entry : sqlSelectMap.entrySet()) {
                if(!entry.getValue().isEmpty()) {
                    sql += entry.getKey() + "= '" + entry.getValue() + "' AND ";
                }
            }

            String partSelectDatePickers = makeSelectStringForDatePicker(startDateValue,endDateValue,salesDateValue,salesEndPredDateValue);

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

    public void checkEditableDatePickers() {
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Отмена поиска по временным интервалам")) {
            salesDate.setDisable(true);
            salesEndPredDate.setDisable(true);
            startDate.setDisable(true);
            endDate.setDisable(true);
        }

        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой открытия и датой закрытия заказа")) {
            salesDate.setDisable(true);
            salesEndPredDate.setDisable(true);

            startDate.setDisable(false);
            endDate.setDisable(false);
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой открытия и датой оплаты заказа")) {
            endDate.setDisable(true);
            salesEndPredDate.setDisable(true);

            startDate.setDisable(false);
            salesDate.setDisable(false);
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой оплаты и датой закрытия заказа")) {
            startDate.setDisable(true);
            salesEndPredDate.setDisable(true);

            endDate.setDisable(false);
            salesDate.setDisable(false);
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал в котором произошла оплата")) {
            startDate.setDisable(true);
            endDate.setDisable(true);

            salesEndPredDate.setDisable(false);
            salesDate.setDisable(false);
        }
    }

    public String makeSelectStringForDatePicker(String startDateValue, String endDateValue, String salesDateValue, String salesEndPredValue) {

        String partOfSelect = "";

        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой открытия и датой закрытия заказа")) {
            String startDateField = "start_date";
            String endDateField = "end_date";

            partOfSelect = startDateField + " BETWEEN '" + startDateValue + "' AND '" + endDateValue + "' AND " +
            endDateField + " BETWEEN '" + startDateValue + "' AND '" + endDateValue + "'";
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой открытия и датой оплаты заказа")) {
            String startDateField = "start_date";
            String salesDateField = "sales_date";

            partOfSelect = startDateField + " BETWEEN '" + startDateValue + "' AND '" + salesDateValue + "' AND " +
                    salesDateField + " BETWEEN '" + startDateValue + "' AND '" + salesDateValue + "'";
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал м/у датой оплаты и датой закрытия заказа")) {
            String salesDateField = "sales_date";
            String endDateField = "end_date";

            partOfSelect = salesDateField + " BETWEEN '" + salesDateValue + "' AND '" + endDateValue + "' AND " +
                    endDateField + " BETWEEN '" + salesDateValue + "' AND '" + endDateValue + "'";
        }
        if (Objects.equals(modeDateSelectBox.getValue().toString(), "Интервал в котором произошла оплата")) {
            String salesDateField = "sales_date";

            partOfSelect = salesDateField + " BETWEEN '" + salesDateValue + "' AND '" + salesEndPredValue + "'";
        }

        return partOfSelect;
    }
}
