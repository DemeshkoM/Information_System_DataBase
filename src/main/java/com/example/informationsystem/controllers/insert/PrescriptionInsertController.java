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
public class PrescriptionInsertController implements InsertController, Initializable {
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
    private TextField diagnosisField;

    @FXML
    private TextField directionForUseField;

    @FXML
    private ChoiceBox patientChoiceBox;

    @FXML
    private ChoiceBox medicineChoiceBox;

    @FXML
    private ChoiceBox doctorChoiceBox;

    private ObservableList<String> itemsPatient = FXCollections.<String>observableArrayList();
    private Map<String, Integer> Patient;

    private ObservableList<String> itemsMedicine = FXCollections.<String>observableArrayList();
    private Map<String, Integer> Medicine;

    private ObservableList<String> itemsDoctor = FXCollections.<String>observableArrayList();
    private Map<String, Integer> Doctor;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        patientChoiceBox.setItems(itemsPatient);
        medicineChoiceBox.setItems(itemsMedicine);
        doctorChoiceBox.setItems(itemsDoctor);

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
        String namePatient = DBInit.getSubstring(" patient_id=", "patient_id=", item);
        String nameMedicine = DBInit.getSubstring(" medicine_id=", "medicine_id=", item);
        String nameDoctor = DBInit.getSubstring(" doctor_id=", "doctor_id=", item);

        String nameAmount = DBInit.getSubstring(" amount=", "amount=", item);
        String nameDiagnosis = DBInit.getSubstring(" diagnosis=", "diagnosis=", item);
        String nameDirection = DBInit.getSubstring(" direction_for_use=", "direction_for_use=", item);
        //System.out.println(item);

        patientChoiceBox.setValue(namePatient);
        medicineChoiceBox.setValue(nameMedicine);
        doctorChoiceBox.setValue(nameDoctor);

        amountField.setText(nameAmount);
        diagnosisField.setText(nameDiagnosis);
        directionForUseField.setText(nameDirection);
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

            if (insertMode == InsertMode.insert) {
                dbInit.insertPrescription(patientId, medicineId, doctorId, Integer.valueOf(amount), diagnosis, direction);
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updatePrescription(id, patientId, medicineId, doctorId, Integer.valueOf(amount), diagnosis, direction);
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}

