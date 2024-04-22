package com.example.informationsystem.controllers.base;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Выполняет обработку окна приветствия Администратора БД.
 *
 * @author Mikhail Demeshko
 */
public class CashierMainController implements Initializable {
    @FXML
    public Button insertPatientButton;

    @FXML
    public Button insertPrescriptionButton;

    @FXML
    public Button listOfPrescriptionsButton;

    @FXML
    public Button listOfPatientsButton;

    @FXML
    public Button listOfReadyMedButton;

    @FXML
    public Button listOfPrepMedButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insertPatientButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_insert_patient.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CashierPatientInsertController tableController = loader.getController();
            try {
                tableController.loadDataPatient();
                tableController.loadDataDoctor();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        insertPrescriptionButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_insert_prescription.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CashierPrescriptionInsertController tableController = loader.getController();

            assert root != null;
            stage.setScene(new Scene(root));
            stage.show();
        });

        listOfPatientsButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            try {
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_search_people.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CashierPatientInsertController tableController = loader.getController();
            try {
                tableController.loadDataPatient();
                tableController.loadDataDoctor();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_prescription_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CashierPrescriptionSearchController tableController = loader.getController();
            try {
                tableController.loadDataView();
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
                root = loader.load(getClass().getResourceAsStream("/com/example/informationsystem/windows/cashier/cashier_ready_medicine_list.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CashierReadyMedSearchController tableController = loader.getController();
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
}
