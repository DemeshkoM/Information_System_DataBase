package com.example.informationsystem.controllers.select;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.example.informationsystem.Main;
import com.example.informationsystem.utils.Connection;

import java.io.IOException;
import java.sql.ResultSet;

public interface SelectController {
    final Connection connection = Main.getConnection();

    default void setListener(ChangeListener listener) {
    }

    default void showAlert(String message, String comment) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Внимание");
        alert.setHeaderText(message);
        alert.setContentText(comment);
        alert.showAndWait();
    }

    ResultSet showResult(String sql);

}
