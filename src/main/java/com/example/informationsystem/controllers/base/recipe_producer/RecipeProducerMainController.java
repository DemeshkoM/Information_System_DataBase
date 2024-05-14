package com.example.informationsystem.controllers.base.recipe_producer;

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

public class RecipeProducerMainController implements Initializable {
    @FXML
    public Button listOfRecipesButton;

    @FXML
    public Button listOfPrescriptionsButton;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = Main.getConnection();

        listOfRecipesButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_search_recipe_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        listOfPrescriptionsButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/recipe_provider/creator_prescription_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            RecipeProducerPrescriptionListController tableController = loader.getController();
            try {
                tableController.loadDataView();
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
