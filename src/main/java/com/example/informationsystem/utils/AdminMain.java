package com.example.informationsystem.utils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.informationsystem.controllers.base.MainController;

import java.io.IOException;
/**
 * Выполняет обработку окна приветствия Администратора БД.
 *
 * @author Mikhail Demeshko
 */
public class AdminMain {
    @FXML
    public Button button;

    @FXML
    public void getAll() {
        showSelectWindow(MainController.FXML);
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    private void showSelectWindow(String name) {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        try {
            Parent root = loader.load(getClass().getResourceAsStream(name));
            primaryStage.setScene(new Scene(root));
        } catch (IOException ignored) {
        }
        primaryStage.show();
    }

}
