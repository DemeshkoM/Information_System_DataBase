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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
public class UserCreateController implements Initializable {
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
    private ComboBox userTypeChoiceBox;

    private Boolean alertCheck = false;

    private ObservableList<String> usersList =  FXCollections.<String>observableArrayList();

    public UserCreateController() {
        connection = Main.getConnection();
        dbInit = new DBInit(connection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersList.addAll("Кассир", "Изготовитель лекарств", "Заведующий подразделения");
        userTypeChoiceBox.setItems(usersList);
    }

    @FXML
    public void createButtonTapped(ActionEvent event) throws IOException, SQLException {
        try {
            alertCheck = false;
            String operation = "";

            String createRoleCashier = "  CREATE PROCEDURE IF NOT EXISTS add_Cashier (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))  " +
                    "  BEGIN  " +
                    "  DECLARE user_name VARCHAR(255);  " +
                    "  DECLARE word VARCHAR(255);  " +
                    "  CREATE ROLE IF NOT EXISTS 'cashier';  " +
                    "    " +
                    "  GRANT EXECUTE ON PROCEDURE dbname.learn_stock_quantity TO 'cashier';  " +
                    "    " +
                    "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.doctor TO 'cashier';  " +
                    "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.patient TO 'cashier';  " +
                    "    " +
                    "    " +
                    "  GRANT SELECT ON dbname.medicine TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.ready_medicine TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.order_status TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.search_prescription TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.search_ready_medicine TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.search_recipe TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.ingredient TO 'cashier';  " +
                    "  GRANT SELECT ON dbname.ingredient_recipe TO 'cashier';  " +
                    "    " +
                    "  GRANT SELECT, INSERT ON dbname.prescription TO 'cashier';  " +
                    "  GRANT SELECT, INSERT ON dbname.production_order TO 'cashier';  " +
                    "  GRANT SELECT, INSERT ON dbname.sales TO 'cashier';  " +
                    "    " +
                    "    " +
                    "    " +
                    "  GRANT UPDATE(diagnosis, direction_for_use) ON dbname.prescription TO 'cashier';  " +
                    "    " +
                    "  GRANT UPDATE(status_id, end_date) ON dbname.production_order TO 'cashier';  " +
                    "    " +
                    "  GRANT UPDATE(sales_date) ON dbname.sales TO 'cashier';  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "    " +
                    "  set @sql = concat(\"DROP USER IF EXISTS '\",`login`,\"'@'\",`host_name`,\"'\");  " +
                    "     PREPARE stmt1 FROM @sql;  " +
                    "     EXECUTE stmt1;  " +
                    "     DEALLOCATE PREPARE stmt1;  " +
                    "       " +
                    "  set @sql = concat(\"CREATE USER IF NOT EXISTS '\",`login`,\"'@'\",`host_name`,\"' IDENTIFIED BY '\",`pass`,\"'  DEFAULT ROLE 'cashier'\");  " +
                    "     PREPARE stmt2 FROM @sql;  " +
                    "     EXECUTE stmt2;  " +
                    "     DEALLOCATE PREPARE stmt2;  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "  END;  ";
            connection.executeQuery(createRoleCashier);

            String createRoleProducer = "  CREATE PROCEDURE IF NOT EXISTS  add_Producer (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))  " +
                    "  BEGIN  " +
                    "  DECLARE user_name VARCHAR(255);  " +
                    "  DECLARE word VARCHAR(255);  " +
                    "  CREATE ROLE IF NOT EXISTS 'recipe_producer';  " +
                    "    " +
                    "  GRANT SELECT ON dbname.patient TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.doctor TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.medicine TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.order_status TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.search_prescription TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.search_recipe TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.prescription TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.ingredient TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.ingredient_recipe TO 'recipe_producer';  " +
                    "  GRANT SELECT ON dbname.production_order TO 'recipe_producer';  " +
                    "    " +
                    "  GRANT UPDATE(status_id) ON dbname.production_order TO 'recipe_producer';  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "    " +
                    "  set @sql = concat(\"DROP USER IF EXISTS '\",`login`,\"'@'\",`host_name`,\"'\");  " +
                    "     PREPARE stmt1 FROM @sql;  " +
                    "     EXECUTE stmt1;  " +
                    "     DEALLOCATE PREPARE stmt1;  " +
                    "       " +
                    "  set @sql = concat(\"CREATE USER IF NOT EXISTS '\",`login`,\"'@'\",`host_name`,\"' IDENTIFIED BY '\",`pass`,\"'  DEFAULT ROLE 'recipe_producer'\");  " +
                    "     PREPARE stmt2 FROM @sql;  " +
                    "     EXECUTE stmt2;  " +
                    "     DEALLOCATE PREPARE stmt2;  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "  END;  ";
            connection.executeQuery(createRoleProducer);

            String createRoleProvider ="  CREATE PROCEDURE IF NOT EXISTS  add_Provider (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))  " +
                    "  BEGIN  " +
                    "  DECLARE user_name VARCHAR(255);  " +
                    "  DECLARE word VARCHAR(255);  " +
                    "  CREATE ROLE IF NOT EXISTS 'provider';  " +
                    "    " +
                    "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medicine TO 'provider';  " +
                    "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.recipe TO 'provider';  " +
                    "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient_recipe TO 'provider';  " +
                    "    " +
                    "  GRANT SELECT ON dbname.medication_types TO 'provider';  " +
                    "  GRANT SELECT ON dbname.medication_diagnosis_description TO 'provider';  " +
                    "  GRANT SELECT ON dbname.search_ready_medicine TO 'provider';  " +
                    "  GRANT SELECT ON dbname.search_recipe TO 'provider';  " +
                    "    " +
                    "  GRANT SELECT, INSERT ON dbname.ready_medicine TO 'provider';  " +
                    "    " +
                    "  GRANT SELECT, INSERT, DELETE ON dbname.ingredient TO 'provider';  " +
                    "    " +
                    "  GRANT UPDATE, DELETE ON dbname.ready_medicine TO 'provider';  " +
                    "    " +
                    "  GRANT UPDATE ON dbname.ingredient TO 'provider';  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "    " +
                    "  set @sql = concat(\"DROP USER IF EXISTS '\",`login`,\"'@'\",`host_name`,\"'\");  " +
                    "     PREPARE stmt1 FROM @sql;  " +
                    "     EXECUTE stmt1;  " +
                    "     DEALLOCATE PREPARE stmt1;  " +
                    "       " +
                    "  set @sql = concat(\"CREATE USER IF NOT EXISTS '\",`login`,\"'@'\",`host_name`,\"' IDENTIFIED BY '\",`pass`,\"'  DEFAULT ROLE 'provider'\");  " +
                    "     PREPARE stmt2 FROM @sql;  " +
                    "     EXECUTE stmt2;  " +
                    "     DEALLOCATE PREPARE stmt2;  " +
                    "    " +
                    "  FLUSH PRIVILEGES;  " +
                    "  END;  ";
            connection.executeQuery(createRoleProvider);

            if(Objects.equals(userTypeChoiceBox.getValue().toString(), "Изготовитель лекарств")) {
                operation = "CALL add_Producer('"+ loginText.getText() + "','" + passwordText.getText() + "','" + hostText.getText() +"')";
            }

            else if(Objects.equals(userTypeChoiceBox.getValue().toString(), "Кассир")) {
                operation = "CALL add_Cashier('"+ loginText.getText() + "','" + passwordText.getText() + "','" + hostText.getText() +"')";
            }

            else if(Objects.equals(userTypeChoiceBox.getValue().toString(), "Заведующий подразделения")) {
                operation = "CALL add_Provider('"+ loginText.getText() + "','" + passwordText.getText() + "','" + hostText.getText() +"')";
            }

            connection.executeQuery(operation);
        } catch (SQLException ex) {
            System.out.println(ex);
            showAlert("error with connection to server", "");
        }
        if (isNotEmpty()  && !alertCheck) {
            showAlert("Завершено", "Пользователь создан. Пользователь может зайти через окно логин/пароль.");
        }
    }

    @FXML
    private void returnToLoginButtonTapped(ActionEvent event) throws SQLException, IOException {
        connection.close();

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/entrance_window.fxml"));
        primaryStage.setScene(new Scene(root));
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


