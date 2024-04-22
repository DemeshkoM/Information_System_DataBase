package com.example.informationsystem.controllers.insert;

import com.example.informationsystem.utils.DBInit;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RecipeInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextArea preparationMethodField;
    @FXML
    private TextField timeHoursField;

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
            ResultSet setId = connection.executeQueryAndGetResult("select * from medicine");
            itemsId.clear();

            if (setId != null) {
                while (setId.next()) {
                    Integer id = setId.getInt(1);
                    System.out.println(id);
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
        String preparationMethod = DBInit.getSubstring(" preparation_method=", "preparation_method=", item);
        String timeHours = DBInit.getSubstring(" time_hours=", "time_hours=", item);

        System.out.println(item);

        idChoiceBox.setValue(id);
        preparationMethodField.setText(preparationMethod);
        timeHoursField.setText(timeHours);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (preparationMethodField.getText().isEmpty() || timeHoursField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String preparationMethod = preparationMethodField.getText();
            String timeHours = timeHoursField.getText();

            String strId = idChoiceBox.getValue().toString();
            int intId = Id.get(Integer.parseInt(strId)-1);

            if (insertMode == InsertMode.insert) {
                dbInit.insertRecipe(intId, preparationMethod, Integer.valueOf(timeHours));
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateRecipe(id, preparationMethod, Integer.valueOf(timeHours));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}