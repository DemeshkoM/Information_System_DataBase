package com.example.informationsystem.controllers.base;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashierPatientInsertController  implements Initializable {
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
    public TableView tableViewPatient;

    @FXML
    public TableView tableViewDoctor;
    @FXML
    public Pagination paginationPatient;
    @FXML
    public Pagination paginationDoctor;

    public CashierPatientInsertController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableViewPatient.setItems(itemsPatient);
        tableViewDoctor.setItems(itemsDoctor);
    }
    @FXML
    private void removeButtonPatientTapped() {
        Object itemToRemove = tableViewPatient.getSelectionModel().getSelectedItem();
        String item = itemToRemove.toString();
        System.out.println(item);
        int id = DBInit.getIdFrom(item);

        connection.delete("DELETE FROM patient" + " WHERE " + " ID LIKE " + id);

        tableViewPatient.getItems().removeAll(itemToRemove);
        try {
            loadDataPatient();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @FXML
    private void removeButtonDoctorTapped() {
        Object itemToRemove = tableViewDoctor.getSelectionModel().getSelectedItem();
        String item = itemToRemove.toString();
        System.out.println(item);
        int id = DBInit.getIdFrom(item);

        connection.delete("DELETE FROM doctor" + " WHERE " + " ID LIKE " + id);

        tableViewDoctor.getItems().removeAll(itemToRemove);
        try {
            loadDataDoctor();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    public void insertButtonPatientTapped() {
        modeChoicePatientDoctor = "InsertPatient";
        configureWindow(InsertMode.insert, modeChoicePatientDoctor);
    }
    @FXML
    public void insertButtonDoctorTapped() {
        modeChoicePatientDoctor = "InsertDoctor";
        configureWindow(InsertMode.insert, modeChoicePatientDoctor);
    }

    @FXML
    private void updateButtonPatientTapped() {
        modeChoicePatientDoctor = "UpdatePatient";
        if(tableViewPatient.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoicePatientDoctor);
        }
    }

    @FXML
    private void updateButtonDoctorTapped() {
        modeChoicePatientDoctor = "UpdateDoctor";
        if(tableViewDoctor.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoicePatientDoctor);
        }
    }

    @FXML
    private void selectButtonPatientTapped() {
        modeChoicePatientDoctor = "SelectPatient";
        configureSelectWindow(modeChoicePatientDoctor);
    }
    @FXML
    private void selectButtonDoctorTapped() {
        modeChoicePatientDoctor = "SelectDoctor";
        configureSelectWindow(modeChoicePatientDoctor);
    }
    public void configureWindow(InsertMode mode, String modeChoice) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                loadDataPatient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        ChangeListener listenerDoctor = (observable, oldValue, newValue) -> {
            try {
                loadDataDoctor();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(Objects.equals(modeChoice, "InsertPatient") || Objects.equals(modeChoice, "UpdatePatient")) {
            Tables tableTypePatient = Tables.getTableByName("patient");


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
                Object itemToUpdate = tableViewPatient.getSelectionModel().getSelectedItem();
                String item = itemToUpdate.toString();
                controller.setItem(item);
                stage.setTitle("Update patient");
            } else {
                stage.setTitle("Insert to patient");
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        }

        else if (Objects.equals(modeChoice, "InsertDoctor") || Objects.equals(modeChoice, "UpdateDoctor")) {
            Tables tableTypeDoctor = Tables.getTableByName("doctor");


            windowName = tableTypeDoctor.getWindowName();
            Stage stageDoctor = new Stage();
            FXMLLoader loaderDoctor = new FXMLLoader();
            Parent rootDoctor = null;
            try {
                rootDoctor = loaderDoctor.load(getClass().getResourceAsStream(windowName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            InsertController controllerDoctor = loaderDoctor.getController();
            controllerDoctor.setListener(listenerDoctor);
            controllerDoctor.setMode(mode);

            if (mode == InsertMode.update) {
                Object itemToUpdate = tableViewDoctor.getSelectionModel().getSelectedItem();
                String item = itemToUpdate.toString();
                controllerDoctor.setItem(item);
                stageDoctor.setTitle("Update doctor");
            } else {
                stageDoctor.setTitle("Insert to doctor");
            }

            assert rootDoctor != null;
            stageDoctor.setScene(new Scene(rootDoctor));
            stageDoctor.show();
        }
    }

    public void loadDataPatient() throws SQLException {
        itemsPatient.clear();
        columnsPatient.clear();
        columnNamesPatient.clear();
        String operation;

        operation = "SELECT * FROM patient";

        // many-many tables
        ResultSet set = connection.executeQueryAndGetResult(operation);
        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsPatient.add(column);
                columnNamesPatient.add(columnName);
            }


            tableViewPatient.getColumns().setAll(columnsPatient);

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

    public void loadDataDoctor() throws SQLException {
        itemsDoctor.clear();
        columnsDoctor.clear();
        columnNamesDoctor.clear();
        String operation;

        operation = "SELECT * FROM doctor";

        // many-many tables
        ResultSet set = connection.executeQueryAndGetResult(operation);
        ResultSetMetaData metaData = set.getMetaData();

        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsDoctor.add(column);
                columnNamesDoctor.add(columnName);
            }


            tableViewDoctor.getColumns().setAll(columnsDoctor);

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
                    map.put(columnNamesDoctor.get(j - 1), value);
                }
                itemsDoctor.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public void configureSelectWindow(String modeChoice) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                this.set = connection.executeQueryAndGetResult(newValue.toString());
                loadDataSelectPatient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        ChangeListener listenerDoctor = (observable, oldValue, newValue) -> {
            try {
                this.set = connection.executeQueryAndGetResult(newValue.toString());
                loadDataSelectDoctor();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(Objects.equals(modeChoice, "SelectPatient")) {
            Requests requestTypePatient = Requests.getRequestByName("select_patient");


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

        else if (Objects.equals(modeChoice, "SelectDoctor")) {
            Requests requestTypeDoctor = Requests.getRequestByName("select_doctor");


            windowName = requestTypeDoctor.getWindowName();
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
            controller.setListener(listenerDoctor);

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    public void loadDataSelectPatient() throws SQLException {
        itemsPatient.clear();
        columnsPatient.clear();
        columnNamesPatient.clear();

        // many-many tables
        ResultSet set = this.set;

        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsPatient.add(column);
                columnNamesPatient.add(columnName);
            }


            tableViewPatient.getColumns().setAll(columnsPatient);

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

    public void loadDataSelectDoctor() throws SQLException {
        itemsDoctor.clear();
        columnsDoctor.clear();
        columnNamesDoctor.clear();

        // many-many tables
        ResultSet set = this.set;
        ResultSetMetaData metaData = set.getMetaData();

        int columnSize = set.getMetaData().getColumnCount();
        try {
            for (int i = 1; i <= columnSize; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsDoctor.add(column);
                columnNamesDoctor.add(columnName);
            }


            tableViewDoctor.getColumns().setAll(columnsDoctor);

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
                    map.put(columnNamesDoctor.get(j - 1), value);
                }
                itemsDoctor.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
