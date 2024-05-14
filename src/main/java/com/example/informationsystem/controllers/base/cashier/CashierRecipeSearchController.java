package com.example.informationsystem.controllers.base.cashier;

import com.example.informationsystem.Main;
import com.example.informationsystem.controllers.base.provider.ProviderRecipeListLookPrescriptionMethodController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.controllers.select.SelectController;
import com.example.informationsystem.requests.Requests;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.utils.Tables;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashierRecipeSearchController implements Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private TextArea preparationMethodField;

    @FXML
    private Button insertReadyMedFormButton;
    @FXML
    private Button insertReadyMedButton;
    @FXML
    private Button insertIngredientForm;
    @FXML
    private Pane medicine;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox ingredientRecipeChoiceBox;

    private ObservableList<String> itemsIngredientRecipeBox = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsIngredientRecipe = new FilteredList<String>(itemsIngredientRecipeBox);
    private Map<String, Integer> IngredientRecipe;

    @FXML
    private Pane ingredient;
    @FXML
    private Pane ingredient_recipe;
    @FXML
    private TableView tableViewRecipe;

    @FXML
    private Pane recipe;
    @FXML
    private TableView tableViewRecipeIngredient;

    private String lastIdRecipe;

    private final Connection connection = Main.getConnection();

    private final LinkedList<TableColumn<Map, String>> columnsViewRecipe = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsViewRecipe = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesViewRecipe = new LinkedList<>();
    private final LinkedList<TableColumn<Map, String>> columnsIngredientRecipe = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsIngredientRecipe = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesIngredientRecipe = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final int rowsPerPage = 8;

    private Object selectedIdRecipe = "test";

    private ResultSet set;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);

        tableViewRecipe.setItems(itemsViewRecipe);
        tableViewRecipeIngredient.setItems(itemsIngredientRecipe);

        tableViewRecipe.setOnMouseClicked(event -> {
            try {
                loadDataIngredientRecipe();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    @FXML
    private void lookPreparationMethodButtonTapped() throws IOException {
        Object itemToUpdate = tableViewRecipe.getSelectionModel().getSelectedItem();
        String item = itemToUpdate.toString();


        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        try {
            root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_recipe_recipe_info.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProviderRecipeListLookPrescriptionMethodController controller = loader.getController();
        controller.setListener(listener);

        controller.setItem(item);
        stage.setTitle("Prescription");

        assert root != null;
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void searchRecipeButtonTapped() {
        configureSelectRecipeViewWindow();
    }

    public void configureSelectRecipeViewWindow() {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                this.set = connection.executeQueryAndGetResult(newValue.toString());
                loadDataSelectRecipe();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(true) {
            Requests requestTypePatient = Requests.getRequestByName("select_recipe");


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

    public void loadDataSelectRecipe() throws SQLException {
        itemsViewRecipe.clear();
        columnsViewRecipe.clear();
        columnNamesViewRecipe.clear();

        // many-many tables
        Requests request = Requests.getRequestByName("select_recipe");

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
                columnsViewRecipe.add(column);
                columnNamesViewRecipe.add(columnName);
            }


            tableViewRecipe.getColumns().setAll(columnsViewRecipe);

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
                    map.put(columnNamesViewRecipe.get(j - 1), value);
                }
                itemsViewRecipe.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadDataIngredientRecipe() throws SQLException {
        itemsIngredientRecipe.clear();
        columnsIngredientRecipe.clear();
        columnNamesIngredientRecipe.clear();
        String operation;

        Object itemToUpdate = "test";
        String item = "test";

        try {
            itemToUpdate = tableViewRecipe.getSelectionModel().getSelectedItem();
            item = itemToUpdate.toString();
            selectedIdRecipe = item;
        }
        catch (NullPointerException exception) {
            item = (String) selectedIdRecipe;
        }

        lastIdRecipe = DBInit.getSubstring(" ID=", "ID=", item);

        operation = "SELECT id_recipe, name_ingredient, amount_ingredient FROM ingredient_recipe " +
                "INNER JOIN ingredient ON(ingredient.id = ingredient_recipe.id_ingredient)" +
                " WHERE id_recipe = " + lastIdRecipe;

        // many-many tables
        Tables table = Tables.getTableByName("ingredient_recipe");

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
                columnsIngredientRecipe.add(column);
                columnNamesIngredientRecipe.add(columnName);
            }


            tableViewRecipeIngredient.getColumns().setAll(columnsIngredientRecipe);

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
                    map.put(columnNamesIngredientRecipe.get(j - 1), value);
                }
                itemsIngredientRecipe.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

