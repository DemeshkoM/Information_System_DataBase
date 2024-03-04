package com.example.informationsystem.controllers.base;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        dbInit.init();
        configureTableView(connection.dataBaseName);
    }

    private void configureTableView(String dataBaseName) {
        try {
            data.clear();
            ResultSet set = connection.executeQueryAndGetResult("SELECT table_name FROM information_schema.tables\n" +
                    "WHERE table_schema = '" + dataBaseName + "';");
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
