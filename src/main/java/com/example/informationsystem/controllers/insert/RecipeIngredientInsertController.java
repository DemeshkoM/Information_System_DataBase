package com.example.informationsystem.controllers.insert;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.*;

public class RecipeIngredientInsertController implements InsertController, Initializable {
    private DBInit dbInit;
    private ChangeListener listener;
    private ObservableStringValue name_obser = new SimpleStringProperty("");
    private InsertMode insertMode = InsertMode.insert;
    private String item;
    @FXML
    private Button insertButton;
    @FXML
    private TextField amountField;

    @FXML
    private ChoiceBox idChoiceBox;

    private ObservableList<String> itemsId = FXCollections.<String>observableArrayList();
    private List<Integer> Id = new ArrayList<Integer>();

    @FXML
    private ChoiceBox idRecipeChoiceBox;
    private ObservableList<String> itemsIdRecipe = FXCollections.<String>observableArrayList();
    private List<Integer> IdRecipe = new ArrayList<Integer>();

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        idChoiceBox.setItems(itemsId);

        try {
            ResultSet setId = connection.executeQueryAndGetResult("select * from ingredient");
            itemsId.clear();

            if (setId != null) {
                while (setId.next()) {
                    Integer id = setId.getInt(1);
                    Id.add(id);
                    itemsId.add(String.valueOf(id));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        idRecipeChoiceBox.setItems(itemsIdRecipe);

        try {
            ResultSet setIdRecipe = connection.executeQueryAndGetResult("select * from recipe");
            itemsIdRecipe.clear();

            if (setIdRecipe != null) {
                while (setIdRecipe.next()) {
                    Integer idRecipe = setIdRecipe.getInt(1);
                    IdRecipe.add(idRecipe);
                    itemsIdRecipe.add(String.valueOf(idRecipe));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMode(InsertMode mode) {
        insertMode = mode;
    }

    public void setItem(String item) {
        this.item = item;
        insertButton.setText("Изменить");

        String amount = DBInit.getSubstring(" Кол-во расходуемого ингр-та в ходе приготовления=", "Кол-во расходуемого ингр-та в ходе приготовления=", item);
        String ingredientId = DBInit.getSubstring(" ID ингредиента=", "ID ингредиента=", item);
        String recipeId = DBInit.getSubstring(" ID рецепта=", "ID рецепта=", item);

        System.out.println(item);

        idChoiceBox.setValue(ingredientId);
        amountField.setText(amount);
        idRecipeChoiceBox.setValue(recipeId);
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (amountField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String amount = amountField.getText();

            String strId = idChoiceBox.getValue().toString();
            int intId = Integer.parseInt(strId);

            String strIdRecipe = idRecipeChoiceBox.getValue().toString();
            int intIdRecipe = Integer.parseInt(strIdRecipe);

            if (insertMode == InsertMode.insert) {
                dbInit.insertRecipeIngredient(intIdRecipe,intId, Integer.valueOf(amount));
            } else {
                dbInit.updateRecipeIngredient(intIdRecipe,intId, Integer.valueOf(amount));
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }
}