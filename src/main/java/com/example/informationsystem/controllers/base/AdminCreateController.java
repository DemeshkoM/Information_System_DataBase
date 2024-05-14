package com.example.informationsystem.controllers.base;

import com.example.informationsystem.Main;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminCreateController implements Initializable {
    private final Connection connection;
    private final DBInit dbInit;
    @FXML
    private TextArea loginText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private TextArea hostText;

    @FXML
    private TextArea portText;

    @FXML
    private ChoiceBox userTypeChoiceBox;

    private Boolean alertCheck = false;

    private ObservableList<String> usersList =  FXCollections.<String>observableArrayList();

    public AdminCreateController() {
        connection = Main.getConnection();
        dbInit = new DBInit(connection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //userTypeChoiceBox.setItems(usersList);
    }

    @FXML
    public void createButtonTapped(ActionEvent event) throws IOException, SQLException {
        try {
            alertCheck = false;
            String operation = "CALL add_Admin('"+ loginText.getText() + "','" + passwordText.getText() + "','" + hostText.getText() +"')";
            connection.executeQuery(operation);
        } catch (SQLException ex) {
            System.out.println(ex);
            showAlert("error with connection to server", "");
        }
        if (isNotEmpty()  && !alertCheck) {
        showAlert("Завершено", "Админ создан. Пользователь может зайти через окно логин/пароль.");
        }
    }

    private boolean isNotEmpty() {
        return loginText.getText().length() != 0 && passwordText.getText().length() != 0; }


    private void showAlert(String header, String content) {
        alertCheck = true;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

