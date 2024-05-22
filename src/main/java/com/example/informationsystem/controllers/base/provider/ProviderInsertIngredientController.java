package com.example.informationsystem.controllers.base.provider;

import com.example.informationsystem.Main;
import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.controllers.select.SelectController;
import com.example.informationsystem.requests.Requests;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.Tables;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProviderInsertIngredientController  implements Initializable {
    private final Connection connection = Main.getConnection();
    private final LinkedList<TableColumn<Map, String>> columnsPatient = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsPatient = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesPatient = new LinkedList<>();
    private final LinkedList<TableColumn<Map, String>> columnsDoctor = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsDoctor = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesDoctor = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final int rowsPerPage = 8;
    private String modeChoicePatientDoctor = "";

    private ResultSet set;

    @FXML
    public TableView tableViewIngredient;

    @FXML
    public TableView tableViewDoctor;
    @FXML
    public Pagination paginationPatient;
    @FXML
    public Pagination paginationDoctor;

    public ProviderInsertIngredientController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableViewIngredient.setItems(itemsPatient);
    }
    @FXML
    public void insertButtonIngredientTapped() {
        modeChoicePatientDoctor = "InsertPatient";
        configureWindow(InsertMode.insert, modeChoicePatientDoctor);
    }
    @FXML
    private void updateButtonPatientTapped() {
        modeChoicePatientDoctor = "UpdatePatient";
        if(tableViewIngredient.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoicePatientDoctor);
        }
    }

    public void configureWindow(InsertMode mode, String modeChoice) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                loadDataIngredient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(Objects.equals(modeChoice, "InsertPatient") || Objects.equals(modeChoice, "UpdatePatient")) {
            Tables tableTypePatient = Tables.getTableByName("ingredient");


            windowName = tableTypePatient.getWindowName();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream(windowName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            InsertController controller = loader.getController();
            controller.setListener(listener);
            controller.setMode(mode);

            if (mode == InsertMode.update) {
                Object itemToUpdate = tableViewIngredient.getSelectionModel().getSelectedItem();
                String item = itemToUpdate.toString();
                controller.setItem(item);
                stage.setTitle("Update ingredient");
            } else {
                stage.setTitle("Insert to ingredient");
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    public void loadDataIngredient() throws SQLException {
        itemsPatient.clear();
        columnsPatient.clear();
        columnNamesPatient.clear();
        String operation;

        operation = "SELECT * FROM ingredient";

        // many-many tables
        Tables table = Tables.getTableByName("ingredient");

        ResultSet set = connection.executeQueryAndGetResult(operation);
        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                //String columnName = metaData.getColumnName(i);
                String columnName = table.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsPatient.add(column);
                columnNamesPatient.add(columnName);
            }


            tableViewIngredient.getColumns().setAll(columnsPatient);

            for (int i = 1; set.next(); ++i) {
                Map<String, Object> map = new HashMap<>();
                for (int j = 1; j <= columnSize; j++) {
                    String value = set.getString(j);
                    if (value == null) {
                        value = "";
                    }
                    try {
                        Date date = formatter.parse(value);
                        value = formatter2.format(date);
                    } catch (ParseException ignore) {
                    }
                    map.put(columnNamesPatient.get(j - 1), value);
                }
                itemsPatient.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void searchIngredientButtonTapped() {
        configureSelectIngredientWindow();
    }

    public void configureSelectIngredientWindow() {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                this.set = connection.executeQueryAndGetResult(newValue.toString());
                loadDataSelectIngredient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(true) {
            Requests requestTypePatient = Requests.getRequestByName("select_ingredient");


            windowName = requestTypePatient.getWindowName();
            System.out.println(windowName);
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream(windowName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            SelectController controller = loader.getController();
            controller.setListener(listener);

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    public void loadDataSelectIngredient() throws SQLException {
        itemsPatient.clear();
        columnsPatient.clear();
        columnsPatient.clear();

        // many-many tables
        Requests request = Requests.getRequestByName("select_ingredient");

        ResultSet set = this.set;

        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                //String columnName = metaData.getColumnName(i);
                String columnName = request.getColumnName(metaData.getColumnName(i));
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsPatient.add(column);
                columnNamesPatient.add(columnName);
            }


            tableViewIngredient.getColumns().setAll(columnsPatient);

            for (int i = 1; set.next(); ++i) {
                Map<String, Object> map = new HashMap<>();
                for (int j = 1; j <= columnSize; j++) {
                    String value = set.getString(j);
                    if (value == null) {
                        value = "";
                    }
                    try {
                        Date date = formatter.parse(value);
                        value = formatter2.format(date);
                    } catch (ParseException ignore) {
                    }
                    map.put(columnNamesPatient.get(j - 1), value);
                }
                itemsPatient.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

