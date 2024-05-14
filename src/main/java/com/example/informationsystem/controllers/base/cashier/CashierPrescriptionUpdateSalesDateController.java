package com.example.informationsystem.controllers.base.cashier;
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
public class CashierPrescriptionUpdateSalesDateController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private DatePicker salesDateField;

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

        String salesDate = DBInit.getSubstring(" Дата оплаты заказа=", "Дата оплаты заказа=", item);

        if(!salesDate.isEmpty()) {
            salesDateField.setValue(LocalDate.parse(salesDate));
        }
    }

    public void updateSalesButtonTapped() {
        LocalDate salesDate = salesDateField.getValue();

        int id = DBInit.getIdFrom(item);

        //dbInit.updateSales(id, Date.valueOf(salesDate));
        dbInit.insertSales(id, Date.valueOf(salesDate));

        listener.changed(name_obser, "", name_obser);
        Stage stage = (Stage) insertButton.getScene().getWindow();
        stage.close();
    }
}

