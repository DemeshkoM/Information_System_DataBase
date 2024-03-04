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
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип сотрудника".
 *
 * @author Mikhail Demeshko
 */
public class EmployeeCategoryTypeInsertController implements InsertController, Initializable {
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
    private ChoiceBox employeeCategoryChoiceBox;

    private ObservableList<String> itemsEmployeeCategory = FXCollections.<String>observableArrayList();
    private Map<String, Integer> EmployeeCategory;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        employeeCategoryChoiceBox.setItems(itemsEmployeeCategory);
        try {
            ResultSet setEmployeeCategory = connection.executeQueryAndGetResult("select * from employee_category");
            EmployeeCategory = new HashMap<>();
            itemsEmployeeCategory.clear();

            if (setEmployeeCategory != null) {
                while (setEmployeeCategory.next()) {
                    String nameCategory = setEmployeeCategory.getString(2);
                    Integer id = setEmployeeCategory.getInt(1);
                    EmployeeCategory.put(nameCategory, id);
                    itemsEmployeeCategory.add(nameCategory);
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
        String nameCategoryType = DBInit.getSubstring(" name_employee_category_type=", "name_employee_category_type=", item);
        String nameCategory = DBInit.getSubstring(" name_employee_category=", "name_employee_category=", item);
        System.out.println(item);

        nameField.setText(nameCategoryType);
        employeeCategoryChoiceBox.setValue(nameCategory);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (nameField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String name = nameField.getText();

            String employeeCategory = employeeCategoryChoiceBox.getValue().toString();
            int employeeCategoryId = EmployeeCategory.get(employeeCategory);

            if (insertMode == InsertMode.insert) {
                dbInit.insertEmployeeCategoryType(name, employeeCategoryId);
            } else {
                int id = DBInit.getIdFrom(item);
                dbInit.updateEmployeeCategoryType(id, name, employeeCategoryId);
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}
