package com.example.informationsystem.controllers.insert;

import com.example.informationsystem.utils.DatePickerFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Категория сотрудника".
 *
 * @author Mikhail Demeshko
 */
public class PatientInsertController implements InsertController, Initializable {
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
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker dateOfBirthField;
    private DatePickerFormatter datePickerFormatter;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        datePickerFormatter = new DatePickerFormatter();

        datePickerFormatter.setDatePickerFormatter(dateOfBirthField);
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    @Override
    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");
        String name = DBInit.getSubstring(" ФИО=", "ФИО=", item);
        String phoneNumber = DBInit.getSubstring(" Номер телефона=", "Номер телефона=", item);
        String address = DBInit.getSubstring(" Адрес=", "Адрес=", item);
        String dateOfBirth = DBInit.getSubstring(" Дата рождения=", "Дата рождения=", item);

        nameField.setText(name);
        phoneField.setText(phoneNumber);
        addressField.setText(address);
        dateOfBirthField.setValue(LocalDate.parse(dateOfBirth));
    }

    public void insertButtonTapped() throws SQLException {
        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || addressField.getText().isEmpty() ||
                Objects.equals(String.valueOf(dateOfBirthField.getValue()), "")) {
            showAlert("empty!", "Fill in required fields");
        } else {
            String name = nameField.getText();
            String phoneNumber = phoneField.getText();
            String address = addressField.getText();
            String dateOfBirth = String.valueOf(dateOfBirthField.getValue());

            if (insertMode == InsertMode.insert) {
                dbInit.insertPatient(name, phoneNumber, address, Date.valueOf(dateOfBirth));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updatePatient(id, name, phoneNumber, address, dateOfBirth);
            }
            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
