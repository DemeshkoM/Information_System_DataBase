package com.example.informationsystem.controllers.insert;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import com.example.informationsystem.utils.Connection;
import com.example.informationsystem.Main;

public interface InsertController {
    Connection connection = Main.getConnection();

    default void setListener(ChangeListener listener) {
    }

    default void showAlert(String message, String comment) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(comment);
        alert.showAndWait();
    }

    default void showConfirmation(String message, String comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(message);
        alert.setContentText(comment);
        alert.showAndWait();
    }

    void setMode(InsertMode mode);
    void setItem(String item);
}

