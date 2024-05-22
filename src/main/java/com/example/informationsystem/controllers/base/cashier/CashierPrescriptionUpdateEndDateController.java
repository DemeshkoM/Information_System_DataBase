package com.example.informationsystem.controllers.base.cashier;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DatePickerFormatter;
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
public class CashierPrescriptionUpdateEndDateController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private DatePicker endDateField;
    private DatePickerFormatter datePickerFormatter;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        datePickerFormatter = new DatePickerFormatter();

        datePickerFormatter.setDatePickerFormatter(endDateField);
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");

        String endDate = DBInit.getSubstring(" Дата закрытия заказа=", "Дата закрытия заказа=", item);
        //endDateField.setValue(LocalDate.parse(endDate));
    }

    public void updateEndDateButtonTapped() throws SQLException {
        LocalDate endDate = endDateField.getValue();

        int id = DBInit.getIdFrom(item);

        dbInit.updateProductionOrderEndDate(id, Date.valueOf(endDate));

        listener.changed(name_obser, "", name_obser);
        Stage stage = (Stage) insertButton.getScene().getWindow();
        stage.close();
    }
}

