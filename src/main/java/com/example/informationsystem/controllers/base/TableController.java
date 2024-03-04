package com.example.informationsystem.controllers.base;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;
import com.example.informationsystem.*;
import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.Tables;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Выполняет обработку окон с таблицами БД, в частности, удаление данных, сортировку (в разработке), фильтрацию (в разработке).
 *
 * @author Mikhail Demeshko
 */
public class TableController implements Initializable {
    public String tableName;
    private final Connection connection = Main.getConnection();
    private final LinkedList<TableColumn<Map, String>> columns = new LinkedList<>();
    private final ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNames = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final int rowsPerPage = 8;

    @FXML
    private TableView tableView;
    @FXML
    private Button filterButton;
    @FXML
    private Pagination pagination;
    @FXML
    private Button sortButton;

    public TableController() {
    }

    @FXML
    private void removeButtonTapped() {
        Object itemToRemove = tableView.getSelectionModel().getSelectedItem();
        String item = itemToRemove.toString();
        System.out.println(item);
        int id = DBInit.getIdFrom(item);
        if (tableName.equals("employee_category") || tableName.equals("employee_category_type")) {
            connection.delete("DELETE FROM " + tableName + " WHERE " + " ID LIKE " + id);
        }

        tableView.getItems().removeAll(itemToRemove);
        try {
            loadData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @FXML
    public void insertButtonTapped() {
        configureWindow(InsertMode.insert);
    }

    @FXML
    private void updateButtonTapped() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            configureWindow(InsertMode.update);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(items);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, items.size());
        tableView.setItems(FXCollections.observableList(items.subList(fromIndex, toIndex)));
        return tableView;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        System.out.println("TABLE NAME: " + tableName);
    }

    public void configureWindow(InsertMode mode) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                loadData();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        Tables tableType = Tables.getTableByName(tableName);
        windowName = tableType.getWindowName();
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
            Object itemToUpdate = tableView.getSelectionModel().getSelectedItem();
            String item = itemToUpdate.toString();
            controller.setItem(item);
            stage.setTitle("Update " + tableName);
        } else {
            stage.setTitle("Insert to " + tableName);
        }
        assert root != null;
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadData() throws SQLException {
        filterButton.setDisable(true);
        sortButton.setDisable(true);


        items.clear();
        columns.clear();
        String operation;

        if (true) {
            operation = "SELECT * FROM " + tableName;
        }
        // many-many tables
        ResultSet set = connection.executeQueryAndGetResult(operation);
        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();
        try {
            for(int i = 1; i <= columnSize; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columns.add(column);
                columnNames.add(columnName);
            }
            tableView.getColumns().setAll(columns);
            for(int i = 1; set.next(); ++i) {
                Map<String, Object> map = new HashMap<>();
                for(int j = 1; j <= columnSize; j++) {
                    String value = set.getString(j);
                    if (value == null) {
                        value = "";
                    }
                    try {
                        Date date = formatter.parse(value);
                        value = formatter2.format(date);
                    } catch (ParseException ignore) {
                    }
                    map.put(columnNames.get(j - 1), value);
                }
                items.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (tableName.equals("REQUEST")) {
            pagination.setPageFactory(this::createPage);
        }
    }

    @FXML
    public void filterButtonTapped () throws IOException {
        Stage stage = new Stage();
        InputStream inputStream = getClass().getResourceAsStream("com/example/informationsystem/windows/filter/order_filter.fxml");
        Parent root = new FXMLLoader().load(inputStream);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void sortButtonTapped() throws IOException {
        Stage stage = new Stage();
        InputStream inputStream = getClass().getResourceAsStream("com/example/informationsystem/windows/sort/sort.fxml");
        Parent root = new FXMLLoader().load(inputStream);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
