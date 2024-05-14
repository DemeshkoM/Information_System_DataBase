package com.example.informationsystem.controllers.base.provider;

import com.example.informationsystem.Main;
import com.example.informationsystem.utils.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProviderMedicineMenuController implements Initializable {
    @FXML
    public Button insertMedicineButton;

    @FXML
    public Button listOfRecipesButton;

    @FXML
    public Button listOfIngredientsButton;

    @FXML
    public Button insertIngredientButton;

    @FXML
    public Button listOfReadyMedButton;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = Main.getConnection();

        insertMedicineButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_insert_medicine.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderMedicineInsertController controller = loader.getController();

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        listOfRecipesButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_recipe_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderRecipeListController controller = loader.getController();

            try {
                controller.loadDataRecipeView();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        listOfIngredientsButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_ingredient_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderIngredientListController controller = loader.getController();

            try {
                controller.loadDataIngredient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        insertIngredientButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_insert_ingredient.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderInsertIngredientController controller = loader.getController();

            try {
                controller.loadDataIngredient();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        listOfReadyMedButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_ready_medicine_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderReadyMedListController controller = loader.getController();

            try {
                controller.loadDataView();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    @FXML
    private void returnToLoginButtonTapped(ActionEvent event) throws SQLException, IOException {
        connection.close();

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/entrance_window.fxml"));
        primaryStage.setScene(new Scene(root));
    }
}
