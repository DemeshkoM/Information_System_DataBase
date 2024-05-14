package com.example.informationsystem.controllers.base.provider;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.controllers.select.SelectController;
import com.example.informationsystem.requests.Requests;
import com.example.informationsystem.utils.DBInit;
import com.example.informationsystem.utils.InputFilter;
import com.example.informationsystem.utils.Tables;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProviderMedicineInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;

    @FXML
    private Button insertReadyMedFormButton;
    @FXML
    private Button insertReadyMedButton;
    @FXML
    private Button insertIngredientForm;
    @FXML
    private Pane medicine;
    @FXML
    private TextField nameField;
    @FXML
    private TextField amountField;

    @FXML
    private ComboBox typeMedChoiceBox;

    @FXML
    private ComboBox diagnosisDescMedChoiceBox;

    @FXML
    private ComboBox checkPrescChoiceBox;
    @FXML
    private ComboBox ingredientRecipeChoiceBox;

    private ObservableList<String> itemsTypeMed = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemstypeMed = new FilteredList<String>(itemsTypeMed);
    private Map<String, Integer> TypeMed;

    private ObservableList<String> itemsDiagnosisDescMed = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsDiagnosisDescMed = new FilteredList<String>(itemsDiagnosisDescMed);
    private Map<String, Integer> DiagnosisDescMed;

    private ObservableList<String> itemsIngredientRecipeBox = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsIngredientRecipe = new FilteredList<String>(itemsIngredientRecipeBox);
    private Map<String, Integer> IngredientRecipe;
    @FXML
    private VBox readyMed;
    @FXML
    private TextField storageQuantityField;
    @FXML
    private TextField criticalQuantityField;
    @FXML
    private TextField priceField;

    @FXML
    private Pane ingredient;
    @FXML
    private Pane ingredient_recipe;
    @FXML
    private TableView tableViewIngredient;

    @FXML
    private Pane recipe;
    @FXML
    private TextArea preparationMethod;
    @FXML
    private TextArea timeHours;
    @FXML
    private TableView tableViewRecipeIngredient;

    private Integer lastIdRecipe;

    private final LinkedList<TableColumn<Map, String>> columnsIngredient = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsIngredient = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesIngredient = new LinkedList<>();
    private final LinkedList<TableColumn<Map, String>> columnsIngredientRecipe = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsIngredientRecipe = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesIngredientRecipe = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final int rowsPerPage = 8;

    private ResultSet set;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        typeMedChoiceBox.getEditor().textProperty().addListener(new InputFilter(typeMedChoiceBox, filteredItemstypeMed, false));
        diagnosisDescMedChoiceBox.getEditor().textProperty().addListener(new InputFilter(diagnosisDescMedChoiceBox, filteredItemsDiagnosisDescMed, false));
        ingredientRecipeChoiceBox.getEditor().textProperty().addListener(new InputFilter(ingredientRecipeChoiceBox, filteredItemsIngredientRecipe, false));

        typeMedChoiceBox.setItems(itemsTypeMed);
        diagnosisDescMedChoiceBox.setItems(itemsDiagnosisDescMed);
        ingredientRecipeChoiceBox.setItems(itemsIngredientRecipeBox);

        ObservableList<String> options = FXCollections.observableArrayList("продается",
                "не продается");

        checkPrescChoiceBox.setItems(options);



        try {
            ResultSet setPatient = connection.executeQueryAndGetResult("select * from medication_types");
            TypeMed = new HashMap<>();
            itemsTypeMed.clear();

            if (setPatient != null) {
                while (setPatient.next()) {
                    String namePatient = setPatient.getString(2);
                    Integer id = setPatient.getInt(1);
                    TypeMed.put(namePatient, id);
                    itemsTypeMed.add(namePatient);
                }
            }

            ResultSet setMedicine = connection.executeQueryAndGetResult("select * from medication_diagnosis_description");
            DiagnosisDescMed = new HashMap<>();
            itemsDiagnosisDescMed.clear();

            if (setMedicine != null) {
                while (setMedicine.next()) {
                    String nameMedicine = setMedicine.getString(2);
                    Integer id = setMedicine.getInt(1);
                    DiagnosisDescMed.put(nameMedicine, id);
                    itemsDiagnosisDescMed.add(nameMedicine);
                }
            }

            ResultSet setIngredientRecipe = connection.executeQueryAndGetResult("select * from ingredient");
            IngredientRecipe = new HashMap<>();
            itemsIngredientRecipeBox.clear();

            if (setIngredientRecipe != null) {
                while (setIngredientRecipe.next()) {
                    String nameMedicine = setIngredientRecipe.getString(2);
                    Integer id = setIngredientRecipe.getInt(1);
                    IngredientRecipe.put(nameMedicine, id);
                    itemsIngredientRecipeBox.add(nameMedicine);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        tableViewIngredient.setItems(itemsIngredient);
        tableViewRecipeIngredient.setItems(itemsIngredientRecipe);

        readyMed.setVisible(false);
        ingredient.setVisible(false);
        recipe.setVisible(false);
        ingredient_recipe.setVisible(false);
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @FXML
    public void insertButtonIngredientTapped() {
        configureIngredientWindow(InsertMode.insert);
    }

    public void configureIngredientWindow(InsertMode mode) {
        String windowName = "";
        ChangeListener listener = (observable, oldValue, newValue) -> {
            try {
                loadDataIngredient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };

        if(true) {
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
        itemsIngredient.clear();
        columnsIngredient.clear();
        columnNamesIngredient.clear();
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
                columnsIngredient.add(column);
                columnNamesIngredient.add(columnName);
            }


            tableViewIngredient.getColumns().setAll(columnsIngredient);

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
                    map.put(columnNamesIngredient.get(j - 1), value);
                }
                itemsIngredient.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void searchIngredientButtonTapped() {
        configureSelectWindow();
    }

    public void configureSelectWindow() {
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
        itemsIngredient.clear();
        columnsIngredient.clear();
        columnNamesIngredient.clear();

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
                columnsIngredient.add(column);
                columnNamesIngredient.add(columnName);
            }


            tableViewIngredient.getColumns().setAll(columnsIngredient);

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
                    map.put(columnNamesIngredient.get(j - 1), value);
                }
                itemsIngredient.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void insertButtonRecipeTapped() {
        if (nameField.getText().isEmpty() || preparationMethod.getText().isEmpty() || timeHours.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String name = nameField.getText();

            String typeMed = typeMedChoiceBox.getValue().toString();
            int typeMedId = TypeMed.get(typeMed);

            String diagnosisDescMed = diagnosisDescMedChoiceBox.getValue().toString();
            int diagnosisDescMedId = DiagnosisDescMed.get(diagnosisDescMed);

            String checkPresc = checkPrescChoiceBox.getValue().toString();

            String preparation = preparationMethod.getText();
            Integer time = Integer.valueOf(timeHours.getText());

            int lastId;

            if (insertMode == InsertMode.insert) {
                lastId = dbInit.insertMedicine(name, typeMedId, diagnosisDescMedId, checkPresc);

                dbInit.insertRecipe(lastId,preparation,time);

                lastIdRecipe = lastId;

                showAlert("Завершено", "Лек-во добавлено в справочник изготовляемых лекарств");
            }

            ingredient_recipe.setDisable(false);
            recipe.setDisable(true);
            medicine.setDisable(true);

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertReadyMedButton.getScene().getWindow();
            stage.close();
        }
    }

    public void insertIngredientRecipeButtonTapped() {
        if (amountField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String amount = amountField.getText();

            String strId = ingredientRecipeChoiceBox.getValue().toString();
            int intId = IngredientRecipe.get(strId);

            if (insertMode == InsertMode.insert) {
                dbInit.insertRecipeIngredient(lastIdRecipe, intId, Integer.valueOf(amount));
            }

            configureIngredientRecipeWindow(InsertMode.insert);

            listener.changed(name_obser, "", name_obser);
        }
    }

    public void configureIngredientRecipeWindow(InsertMode mode) {
        String windowName = "";
        this.listener = (observable, oldValue, newValue) -> {
            try {
                loadDataIngredientRecipe();
                tableViewRecipeIngredient.refresh();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
    }

    public void loadDataIngredientRecipe() throws SQLException {
        itemsIngredientRecipe.clear();
        columnsIngredientRecipe.clear();
        columnNamesIngredientRecipe.clear();
        String operation;

        operation = "SELECT * FROM ingredient_recipe WHERE id_recipe = " + lastIdRecipe;

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

    public void insertReadyMedFormButtonTapped(ActionEvent actionEvent) throws SQLException {
        readyMed.setVisible(true);
        insertIngredientForm.setDisable(true);
    }

    public void insertIngredientFormButtonTapped(ActionEvent actionEvent) throws SQLException {
        ingredient.setVisible(true);
        insertReadyMedFormButton.setDisable(true);
    }

    public void insertRecipeFormButtonTapped(ActionEvent actionEvent) throws SQLException {
        ingredient.setVisible(true);
        recipe.setVisible(true);
        insertReadyMedFormButton.setDisable(true);

        ingredient_recipe.setVisible(true);
        ingredient_recipe.setDisable(true);
    }

    public void insertReadyMedButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (nameField.getText().isEmpty() || storageQuantityField.getText().isEmpty() || criticalQuantityField.getText().isEmpty()
        || priceField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String name = nameField.getText();
            Integer storageQuantity = Integer.valueOf(storageQuantityField.getText());
            Integer criticalQuantity = Integer.valueOf(criticalQuantityField.getText());
            Integer price = Integer.valueOf(priceField.getText());

            String typeMed = typeMedChoiceBox.getValue().toString();
            int typeMedId = TypeMed.get(typeMed);

            String diagnosisDescMed = diagnosisDescMedChoiceBox.getValue().toString();
            int diagnosisDescMedId = DiagnosisDescMed.get(diagnosisDescMed);

            String checkPresc = checkPrescChoiceBox.getValue().toString();

            int lastId;

            if (insertMode == InsertMode.insert) {

                    lastId = dbInit.insertMedicine(name, typeMedId, diagnosisDescMedId, checkPresc);

                    dbInit.insertReadyMedicine(lastId, storageQuantity, criticalQuantity, price);
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertReadyMedButton.getScene().getWindow();
            stage.close();
        }
    }

    public void cleanFormButtonTapped(ActionEvent actionEvent) throws SQLException {
        readyMed.setVisible(false);
        ingredient.setVisible(false);
        recipe.setVisible(false);
        ingredient_recipe.setVisible(false);

        medicine.setDisable(false);
        recipe.setDisable(false);

        ingredient_recipe.setDisable(true);

        insertReadyMedFormButton.setDisable(false);
        insertIngredientForm.setDisable(false);

        nameField.clear();
        typeMedChoiceBox.getSelectionModel().clearSelection();
        diagnosisDescMedChoiceBox.getSelectionModel().clearSelection();
        checkPrescChoiceBox.getSelectionModel().clearSelection();

        storageQuantityField.clear();
        criticalQuantityField.clear();
        priceField.clear();

        preparationMethod.clear();
        timeHours.clear();
    }
}
