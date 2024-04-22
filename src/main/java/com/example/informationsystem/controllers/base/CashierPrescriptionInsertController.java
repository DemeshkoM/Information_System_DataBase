package com.example.informationsystem.controllers.base;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.InputFilter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class CashierPrescriptionInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField amountField;

    @FXML
    private TextArea diagnosisField;

    @FXML
    private TextArea directionForUseField;

    @FXML
    private ComboBox patientChoiceBox;

    @FXML
    private ComboBox medicineChoiceBox;

    @FXML
    private ComboBox doctorChoiceBox;

    private ObservableList<String> itemsPatient = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsPatient = new FilteredList<String>(itemsPatient);
    private Map<String, Integer> Patient;

    private ObservableList<String> itemsMedicine = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsMedicine = new FilteredList<String>(itemsMedicine);
    private Map<String, Integer> Medicine;

    private ObservableList<String> itemsDoctor = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsDoctor = new FilteredList<String>(itemsDoctor);
    private Map<String, Integer> Doctor;
    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;
    @FXML
    private DatePicker salesDateField;
    @FXML
    private ChoiceBox orderStatusChoiceBox;
    private ObservableList<String> itemsOrderStatus = FXCollections.<String>observableArrayList();
    private Map<String, Integer> OrderStatus;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        patientChoiceBox.getEditor().textProperty().addListener(new InputFilter(patientChoiceBox, filteredItemsPatient, false));
        medicineChoiceBox.getEditor().textProperty().addListener(new InputFilter(medicineChoiceBox, filteredItemsMedicine, false));
        doctorChoiceBox.getEditor().textProperty().addListener(new InputFilter(doctorChoiceBox, filteredItemsDoctor, false));

        patientChoiceBox.setItems(itemsPatient);
        medicineChoiceBox.setItems(itemsMedicine);
        doctorChoiceBox.setItems(itemsDoctor);
        orderStatusChoiceBox.setItems(itemsOrderStatus);



        medicineChoiceBox.setOnAction(event -> {
            try {
                checkMedicineChoiceBoxOnReadyMed(medicineChoiceBox);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

            orderStatusChoiceBox.setItems(itemsOrderStatus);
            try {
                ResultSet setOrderStatus = connection.executeQueryAndGetResult("select * from order_status");
                OrderStatus = new HashMap<>();
                itemsOrderStatus.clear();

                if (setOrderStatus != null) {
                    while (setOrderStatus.next()) {
                        String nameType = setOrderStatus.getString(2);
                        Integer id = setOrderStatus.getInt(1);
                        OrderStatus.put(nameType, id);
                        itemsOrderStatus.add(nameType);
                    }
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
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
        String namePatient = DBInit.getSubstring(" patient=", "patient=", item);
        String nameMedicine = DBInit.getSubstring(" medicine=", "medicine=", item);
        String nameDoctor = DBInit.getSubstring(" doctor=", "doctor=", item);

        String nameAmount = DBInit.getSubstring(" amount=", "amount=", item);
        String nameDiagnosis = DBInit.getSubstring(" diagnosis=", "diagnosis=", item);
        String nameDirection = DBInit.getSubstring(" direction_for_use=", "direction_for_use=", item);

        String startDate = DBInit.getSubstring(" start_date=", "start_date=", item);
        String endDate = DBInit.getSubstring(" end_date=", "end_date=", item);
        String orderStatus = DBInit.getSubstring(" order_status=", "order_status=", item);

        String salesDate = DBInit.getSubstring(" sales_date=", "sales_date=", item);
        //System.out.println(item);


        patientChoiceBox.setValue(namePatient);
        medicineChoiceBox.setValue(nameMedicine);
        doctorChoiceBox.setValue(nameDoctor);

        amountField.setText(nameAmount);
        diagnosisField.setText(nameDiagnosis);
        directionForUseField.setText(nameDirection);


        Object orderStatusKey = OrderStatus.keySet().toArray()[Integer.parseInt(orderStatus)];

        orderStatusChoiceBox.setValue(orderStatusKey);
        startDateField.setText(startDate);
        endDateField.setText(endDate);

        salesDateField.setValue(LocalDate.parse(salesDate));
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (amountField.getText().isEmpty() || diagnosisField.getText().isEmpty() || directionForUseField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String amount = amountField.getText();
            String diagnosis = diagnosisField.getText();
            String direction = directionForUseField.getText();

            String patient = patientChoiceBox.getValue().toString();
            int patientId = Patient.get(patient);

            String medicine = medicineChoiceBox.getValue().toString();
            int medicineId = Medicine.get(medicine);

            String doctor = doctorChoiceBox.getValue().toString();
            int doctorId = Doctor.get(doctor);

            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            int lastId;

            if (insertMode == InsertMode.insert) {
                lastId = dbInit.insertPrescription(patientId, medicineId, doctorId, Integer.valueOf(amount), diagnosis, direction);

                if(!orderStatusChoiceBox.isDisabled()) {
                    String orderStatus = orderStatusChoiceBox.getValue().toString();
                    int orderStatusId = OrderStatus.get(orderStatus);
                    dbInit.insertProductionOrder(lastId, orderStatusId, Date.valueOf(startDate), Date.valueOf(endDate));
                }
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }

    public void checkMedicineChoiceBoxOnReadyMed(ComboBox Med) throws SQLException {
        ResultSet setReady = connection.executeQueryAndGetResult("select * from ready_medicine");
        List<Integer> Ready = new ArrayList<>();

        if (setReady != null) {
            while (setReady.next()) {
                Integer idReady = setReady.getInt(1);
                Ready.add(idReady);
            }
        }

        if(Ready.contains(Medicine.get(Med.getValue()))) {
            orderStatusChoiceBox.setDisable(true);
            startDateField.setDisable(true);
            endDateField.setDisable(true);
        }

        else {
            orderStatusChoiceBox.setDisable(false);
            startDateField.setDisable(false);
            endDateField.setDisable(false);
        }
    }

    public void helpButtonTapped() {
        dbInit.showAlert("Подсказка", "Если хотите продать лек-во не требующее рецепта:\n" +
                "1) Выбираете лек-во\n" +
                "2) Указываете кол-во\n" +
                "3) Проставить '-' на остальных позициях или вставить нужные данные при необх-ти ");
    }
}
