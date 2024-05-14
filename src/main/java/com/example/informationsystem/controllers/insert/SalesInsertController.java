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
import java.sql.Date;
import java.util.*;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class SalesInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField salesDateField;

    @FXML
    private ChoiceBox idChoiceBox;

    private ObservableList<String> itemsId = FXCollections.<String>observableArrayList();
    private List<Integer> Id = new ArrayList<Integer>();;


    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        idChoiceBox.setItems(itemsId);

        try {
            ResultSet setId = connection.executeQueryAndGetResult("select * from prescription");
            itemsId.clear();

            if (setId != null) {
                while (setId.next()) {
                    Integer id = setId.getInt(1);
                    Id.add(id);
                    itemsId.add(String.valueOf(id));
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

        Integer id = DBInit.getIdFrom(item);
        String salesDate = DBInit.getSubstring(" Дата оплаты заказа=", "Дата оплаты заказа=", item);

        System.out.println(item);

        idChoiceBox.setValue(id);
        salesDateField.setText(salesDate);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (salesDateField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String salesDate = salesDateField.getText();

            String strId = idChoiceBox.getValue().toString();
            int intId = Integer.parseInt(strId);

            if (insertMode == InsertMode.insert) {
                dbInit.insertSales(intId, Date.valueOf(salesDate));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateSales(id, Date.valueOf(salesDate));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}