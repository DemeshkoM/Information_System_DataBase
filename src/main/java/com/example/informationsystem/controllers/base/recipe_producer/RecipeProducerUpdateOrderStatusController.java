package com.example.informationsystem.controllers.base.recipe_producer;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.DBInit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RecipeProducerUpdateOrderStatusController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
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
        orderStatusChoiceBox.setItems(itemsOrderStatus);

        try {
            ResultSet setOrderStatus = connection.executeQueryAndGetResult("select * from order_status WHERE id = 4");
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
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");

        String orderStatus = DBInit.getSubstring(" Текущий статус заказа=", "Текущий статус заказа=", item);

        orderStatusChoiceBox.setValue(orderStatus);
    }
    public void updateOrderStatusButtonTapped() {

        String orderStatus = orderStatusChoiceBox.getValue().toString();
        int orderStatusId = OrderStatus.get(orderStatus);

        int id = DBInit.getIdFrom(item);

        dbInit.updateProductionOrderStatus(id, orderStatusId);

        listener.changed(name_obser, "", name_obser);
        Stage stage = (Stage) insertButton.getScene().getWindow();
        stage.close();
    }
}

