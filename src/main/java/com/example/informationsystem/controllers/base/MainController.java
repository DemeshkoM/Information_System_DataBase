package com.example.informationsystem.controllers.base;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
/**
 * Выполняет обработку окна со списком доступных таблиц БД.
 *
 * @author Mikhail Demeshko
 */

public class MainController implements Initializable {
    public final static String FXML = "/com/example/informationsystem/windows/main_window.fxml";
    private final Connection connection;
    private final DBInit dbInit;
    public static final ObservableList data = FXCollections.observableArrayList();

    @FXML
    private ListView<String> tableListView;

    public MainController() {
        connection = Main.getConnection();
        dbInit = new DBInit(connection);
        //dbInit.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableListView.setItems(data);
        configureTableView(connection.dataBaseName);
        tableListView.setOnMouseClicked(event -> {
            System.out.println("clicked on " + tableListView.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            stage.setTitle(tableListView.getSelectionModel().getSelectedItem());

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/table_window.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            TableController tableController = loader.getController();
            String parameter = tableListView.getSelectionModel().getSelectedItem();
            tableController.setTableName(parameter);
            try {
                tableController.loadData();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    @FXML
    private void createButtonTapped() throws SQLException {
        connection.createDB();
        dbInit.init();
        configureTableView(connection.dataBaseName);
    }
    @FXML
    private void createAdminButtonTapped() throws SQLException, IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        System.out.println("++++");
        root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/create_admin_window.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void createAllUsersButtonTapped() throws SQLException {
        String operation = "  CALL add_Admin('db_admin','1','localhost');";
        connection.executeQuery(operation);

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
                "GRANT SELECT ON dbname.medication_types TO 'cashier'; " +
                "GRANT SELECT ON dbname.medication_diagnosis_description TO 'cashier';" +
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
                "GRANT SELECT ON dbname.medication_types TO 'recipe_producer'; " +
                "GRANT SELECT ON dbname.medication_diagnosis_description TO 'recipe_producer';" +
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
        connection.executeQuery("CALL add_Cashier('db_cashier','3','localhost');");
        connection.executeQuery("CALL add_Producer('db_producer','5','localhost');");
        connection.executeQuery("CALL add_Provider('db_provider','7','localhost');");

        showConfirmation("Успешно", "Тестовые пользователи созданы и имеют неоходимые роли.");
    }

    public void showConfirmation(String message, String comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(message);
        alert.setContentText(comment);
        alert.showAndWait();
    }

    @FXML
    private void dropDataBaseButtonTapped() throws SQLException {
        String operation = "  CREATE PROCEDURE IF NOT EXISTS drop_db()  " +
                "  BEGIN  " +
                "  DROP DATABASE IF EXISTS dbname;  " +
                "    " +
                "  DROP USER IF EXISTS 'db_provider'@'localhost';  " +
                "  DROP USER IF EXISTS 'db_producer'@'localhost';  " +
                "  DROP USER IF EXISTS 'db_cashier'@'localhost';  " +
                "    " +
                "  DROP ROLE IF EXISTS 'recipe_producer';  " +
                "  DROP ROLE IF EXISTS 'provider';  " +
                "  DROP ROLE IF EXISTS 'cashier';  " +
                "    " +
                "  DROP USER IF EXISTS 'db_admin'@'localhost';  " +
                "    " +
                "  DROP ROLE IF EXISTS 'pharmacy_db_admin';  " +
                "  FLUSH PRIVILEGES;  " +
                "  END;  ";

        connection.executeQuery(operation);
        connection.executeQuery("CALL drop_db();");
        configureTableView(connection.dataBaseName);
    }

    @FXML
    private void returnToLoginButtonTapped(ActionEvent event) throws SQLException, IOException {
        connection.close();

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/entrance_window.fxml"));
        primaryStage.setScene(new Scene(root));
    }

    @FXML
    private void dropUsersAndRolesButtonTapped() throws SQLException {

    }

    private void configureTableView(String dataBaseName) {
        try {
            data.clear();
            ResultSet set = connection.executeQueryAndGetResult("SELECT table_name FROM information_schema.tables\n" +
                    "WHERE table_schema = 'DBName';");
            if (set != null) {
                while (set.next()) {
                    String name = set.getString(1);
                    data.add(name);
                }
            }
            tableListView.refresh();
        } catch(SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
