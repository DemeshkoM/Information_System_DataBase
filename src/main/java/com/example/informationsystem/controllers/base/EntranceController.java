package com.example.informationsystem.controllers.base;

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
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Выполняет обработку окна авторизации.
 *
 * @author Mikhail Demeshko
 */
public class EntranceController implements Initializable {
    public final static String LOGIN_WINDOW_FXML = "/com/example/informationsystem/windows/entrance_window.fxml";
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

    public EntranceController() {
        connection = Main.getConnection();
        dbInit = new DBInit(connection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersList.addAll("Кассир", "Админ");

        userTypeChoiceBox.setItems(usersList);
    }

    @FXML
    public void loginButtonTapped(ActionEvent event) throws IOException {
        try {
            alertCheck = false;
            connection.registerConnection(loginText.getText(), passwordText.getText(), hostText.getText(), portText.getText());
        } catch (SQLException ex) {
            System.out.println("SQLException: error with connection to server");
            showAlert("error with connection to server", "");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: error with driver manager");
            showAlert("error with driver manager", "");
        }
        if (isNotEmpty()  && !alertCheck) {
            try {
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader();

                if(Objects.equals(userTypeChoiceBox.getValue().toString(), "Админ")) {
                    Parent root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/admin_main_window.fxml"));
                    primaryStage.setScene(new Scene(root));
                }

                else if(Objects.equals(userTypeChoiceBox.getValue().toString(), "Кассир")) {
                    Parent root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_main_window.fxml"));
                    primaryStage.setScene(new Scene(root));
                }
            } catch (ExceptionInInitializerError ex) {
                System.out.println("ExceptionInInitializerError: session is already exist");
                showAlert("session is already exist", "");
            }
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
