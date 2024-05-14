package com.example.informationsystem.controllers.base.provider;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Выполняет обработку окна приветствия Администратора БД.
 *
 * @author Mikhail Demeshko
 */
public class ProviderMainController implements Initializable {
    @FXML
    public Button openProviderRequestMenu;

    @FXML
    public Button openProviderMedicineMenu;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        openProviderMedicineMenu.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/provider/provider_medicine_main_window.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProviderMedicineMenuController controller = loader.getController();

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}

