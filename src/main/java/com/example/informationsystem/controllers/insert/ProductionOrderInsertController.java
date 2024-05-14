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
public class ProductionOrderInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;
    @FXML
    private ChoiceBox orderStatusChoiceBox;

    @FXML
    private ChoiceBox idChoiceBox;

    private ObservableList<String> itemsOrderStatus = FXCollections.<String>observableArrayList();
    private Map<String, Integer> OrderStatus;

    private ObservableList<String> itemsId = FXCollections.<String>observableArrayList();
    private List<Integer> Id = new ArrayList<Integer>();


    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
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
        String startDate = DBInit.getSubstring(" Дата открытия заказа=", "Дата открытия заказа=", item);
        String endDate = DBInit.getSubstring(" Дата закрытия заказа=", "Дата закрытия заказа=", item);
        String orderStatus = DBInit.getSubstring(" ID статуса=", "ID статуса=", item);

        System.out.println(item);

        idChoiceBox.setValue(id);
        orderStatusChoiceBox.setValue(orderStatus);
        startDateField.setText(startDate);
        endDateField.setText(endDate);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (startDateField.getText().isEmpty() || endDateField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            String orderStatus = orderStatusChoiceBox.getValue().toString();
            int orderStatusId = OrderStatus.get(orderStatus);

            String strId = idChoiceBox.getValue().toString();
            int intId = Integer.parseInt(strId);

            if (insertMode == InsertMode.insert) {
                dbInit.insertProductionOrder(intId, orderStatusId, Date.valueOf(startDate), Date.valueOf(endDate));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateProductionOrder(intId, orderStatusId, Date.valueOf(startDate), Date.valueOf(endDate));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}

