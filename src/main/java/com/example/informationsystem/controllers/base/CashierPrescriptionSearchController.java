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

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashierPrescriptionSearchController  implements Initializable {
    private final Connection connection = Main.getConnection();
    private final LinkedList<TableColumn<Map, String>> columnsView = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsView = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesView = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final int rowsPerPage = 8;
    private String modeChoiceView = "";

    private ResultSet set;

    @FXML
    public TableView tableViewSearch;
    @FXML
    public Pagination paginationView;

    public CashierPrescriptionSearchController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableViewSearch.setItems(itemsView);
    }

    @FXML
    private void updateEndDateButtonTapped() {
        modeChoiceView = "view_search_prescription_end_date";
        if(tableViewSearch.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoiceView);
        }
    }

    @FXML
    private void updateSalesDateButtonTapped() {
        modeChoiceView = "view_search_prescription_sales_date";
        if(tableViewSearch.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoiceView);
        }
    }

    @FXML
    private void updateOrderStatusButtonTapped() {
        modeChoiceView = "view_search_prescription_order_status";
        if(tableViewSearch.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoiceView);
        }
    }
    @FXML
    private void updateSecondAttributesButtonTapped() {
        modeChoiceView = "view_search_prescription_second_attributes";
        if(tableViewSearch.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update, modeChoiceView);
        }
    }

    @FXML
    private void selectButtonTapped() {
        modeChoiceView = "SelectPrescription";
        configureSelectWindow(modeChoiceView);
    }
    public void configureWindow(InsertMode mode, String modeChoice) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                loadDataView();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        Tables tableTypeView = Tables.getTableByName("view_search_prescription_end_date");

        if(Objects.equals(modeChoice, "view_search_prescription_sales_date")) {
            tableTypeView = Tables.getTableByName("view_search_prescription_sales_date");
        }
        else if(Objects.equals(modeChoice, "view_search_prescription_order_status")) {
            tableTypeView = Tables.getTableByName("view_search_prescription_order_status");
        }
        else if(Objects.equals(modeChoice, "view_search_prescription_second_attributes")) {
            tableTypeView = Tables.getTableByName("view_search_prescription_second_attributes");
        }

        windowName = tableTypeView.getWindowName();
        System.out.println(windowName);
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        try {
            root = loader.load(getClass().getResourceAsStream(windowName));
            System.out.println(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InsertController controller = loader.getController();
        controller.setListener(listener);
        controller.setMode(mode);

        if (mode == InsertMode.update) {
            Object itemToUpdate = tableViewSearch.getSelectionModel().getSelectedItem();
            String item = itemToUpdate.toString();
            controller.setItem(item);
            stage.setTitle("Update view");
        } else {
            stage.setTitle("Insert to view");
        }

        assert root != null;
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadDataView() throws SQLException {
        itemsView.clear();
        columnsView.clear();
        columnNamesView.clear();
        String operation;

        operation = "SELECT * FROM search_prescription";

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
                columnsView.add(column);
                columnNamesView.add(columnName);
            }


            tableViewSearch.getColumns().setAll(columnsView);

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
                    map.put(columnNamesView.get(j - 1), value);
                }
                itemsView.add(map);
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

        if(Objects.equals(modeChoice, "SelectPrescription")) {
            Requests requestTypePatient = Requests.getRequestByName("select_prescription");


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

    public void loadDataSelectPatient() throws SQLException {
        itemsView.clear();
        columnsView.clear();
        columnNamesView.clear();

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
                columnsView.add(column);
                columnNamesView.add(columnName);
            }


            tableViewSearch.getColumns().setAll(columnsView);

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
                    map.put(columnNamesView.get(j - 1), value);
                }
                itemsView.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

