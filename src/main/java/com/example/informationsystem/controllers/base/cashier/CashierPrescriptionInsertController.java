package com.example.informationsystem.controllers.base.cashier;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.InsertMode;
import com.example.informationsystem.utils.InputFilter;
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
import javafx.stage.Stage;
import com.example.informationsystem.utils.DBInit;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Выполняет обработку добавления/изменения данных в окне с таблицей "Тип лекарства".
 *
 * @author Mikhail Demeshko
 */
public class CashierPrescriptionInsertController implements InsertController, Initializable {
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
    private TextArea diagnosisField;

    @FXML
    private TextArea directionForUseField;

    @FXML
    private ComboBox patientChoiceBox;

    @FXML
    private ComboBox medicineChoiceBox;

    @FXML
    private ComboBox doctorChoiceBox;

    private ObservableList<String> itemsPatient = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsPatient = new FilteredList<String>(itemsPatient);
    private Map<String, Integer> Patient;

    private ObservableList<String> itemsMedicine = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsMedicine = new FilteredList<String>(itemsMedicine);
    private Map<String, Integer> Medicine;

    private ObservableList<String> itemsDoctor = FXCollections.<String>observableArrayList();
    private FilteredList<String> filteredItemsDoctor = new FilteredList<String>(itemsDoctor);
    private Map<String, Integer> Doctor;
    @FXML
    private DatePicker startDateField;

    @FXML
    private DatePicker endDateField;
    @FXML
    private DatePicker salesDateField;
    @FXML
    private ChoiceBox orderStatusChoiceBox;
    private ObservableList<String> itemsOrderStatus = FXCollections.<String>observableArrayList();
    private Map<String, Integer> OrderStatus;
    @FXML
    private TableView informationOnStockView;
    private final LinkedList<TableColumn<Map, String>> columnsInformationOnStockView = new LinkedList<>();
    private final ObservableList<Map<String, Object>> itemsInformationOnStockView = FXCollections.<Map<String, Object>>observableArrayList();
    private final LinkedList<String> columnNamesInformationOnStockView = new LinkedList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private final SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    @FXML
    private Button closeWindowButton;
    @FXML
    private Pane productionOrderPane;
    @FXML
    private Pane patientDoctorPane;
    @FXML
    private Pane learnStockPane;

    @Override
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbInit = new DBInit(connection);
        patientChoiceBox.getEditor().textProperty().addListener(new InputFilter(patientChoiceBox, filteredItemsPatient, false));
        medicineChoiceBox.getEditor().textProperty().addListener(new InputFilter(medicineChoiceBox, filteredItemsMedicine, false));
        doctorChoiceBox.getEditor().textProperty().addListener(new InputFilter(doctorChoiceBox, filteredItemsDoctor, false));

        patientChoiceBox.setItems(itemsPatient);
        medicineChoiceBox.setItems(itemsMedicine);
        doctorChoiceBox.setItems(itemsDoctor);
        orderStatusChoiceBox.setItems(itemsOrderStatus);

        informationOnStockView.setItems(itemsInformationOnStockView);
        learnStockPane.setVisible(false);


        medicineChoiceBox.setOnAction(event -> {
            try {
                checkMedicineChoiceBoxOnReadyMed(medicineChoiceBox);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            ResultSet setPatient = connection.executeQueryAndGetResult("select * from patient");
            Patient = new HashMap<>();
            itemsPatient.clear();

            if (setPatient != null) {
                while (setPatient.next()) {
                    String namePatient = setPatient.getString(2);
                    Integer id = setPatient.getInt(1);
                    Patient.put(namePatient, id);
                    itemsPatient.add(namePatient);
                }
            }

            ResultSet setMedicine = connection.executeQueryAndGetResult("select * from medicine");
            Medicine = new HashMap<>();
            itemsMedicine.clear();

            if (setMedicine != null) {
                while (setMedicine.next()) {
                    String nameMedicine = setMedicine.getString(2);
                    Integer id = setMedicine.getInt(1);
                    Medicine.put(nameMedicine, id);
                    itemsMedicine.add(nameMedicine);
                }
            }

            ResultSet setDoctor = connection.executeQueryAndGetResult("select * from doctor");
            Doctor = new HashMap<>();
            itemsDoctor.clear();

            if (setDoctor != null) {
                while (setDoctor.next()) {
                    String nameDoctor = setDoctor.getString(2);
                    Integer id = setDoctor.getInt(1);
                    Doctor.put(nameDoctor, id);
                    itemsDoctor.add(nameDoctor);
                }
            }

            orderStatusChoiceBox.setItems(itemsOrderStatus);
            try {
                ResultSet setOrderStatus = connection.executeQueryAndGetResult("select * from order_status " +
                        "WHERE name_order_status = 'Ожидание доставки ингредиентов' OR name_order_status = 'Ожидание оплаты'");
                OrderStatus = new HashMap<>();
                itemsOrderStatus.clear();

                if (setOrderStatus != null) {
                    while (setOrderStatus.next()) {
                        String nameType = setOrderStatus.getString(2);
                        Integer id = setOrderStatus.getInt(1);
                        OrderStatus.put(nameType, id);
                        itemsOrderStatus.add(nameType);
                    }
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
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
        String namePatient = DBInit.getSubstring(" ФИО пациента=", "ФИО пациента=", item);
        String nameMedicine = DBInit.getSubstring(" Название лек-ва=", "Название лек-ва=", item);
        String nameDoctor = DBInit.getSubstring(" ФИО доктора=", "ФИО доктора=", item);

        String nameAmount = DBInit.getSubstring(" Кол-во требуемого лек-ва=", "Кол-во требуемого лек-ва=", item);
        String nameDiagnosis = DBInit.getSubstring(" Диагноз из рецепта=", "Диагноз из рецепта=", item);
        String nameDirection = DBInit.getSubstring(" Способ применения из рецепта=", "Способ применения из рецепта=", item);

        String startDate = DBInit.getSubstring(" Дата открытия заказа=", "Дата открытия заказа=", item);
        String endDate = DBInit.getSubstring(" Дата закрытия заказа=", "Дата закрытия заказа=", item);
        String orderStatus = DBInit.getSubstring(" Текущий статус заказа=", "Текущий статус заказа=", item);

        String salesDate = DBInit.getSubstring(" Дата оплаты заказа=", "Дата оплаты заказа=", item);
        //System.out.println(item);


        patientChoiceBox.setValue(namePatient);
        medicineChoiceBox.setValue(nameMedicine);
        doctorChoiceBox.setValue(nameDoctor);

        amountField.setText(nameAmount);
        diagnosisField.setText(nameDiagnosis);
        directionForUseField.setText(nameDirection);


        Object orderStatusKey = OrderStatus.keySet().toArray()[Integer.parseInt(orderStatus)];

        orderStatusChoiceBox.setValue(orderStatusKey);
        startDateField.setValue(LocalDate.parse(startDate));
        endDateField.setValue(LocalDate.parse(endDate));

        salesDateField.setValue(LocalDate.parse(salesDate));
    }

    public void insertButtonTapped(ActionEvent actionEvent) throws SQLException {
        if (amountField.getText().isEmpty() || diagnosisField.getText().isEmpty() || directionForUseField.getText().isEmpty()) {
            showAlert("empty!", "Fill in required fields");

        } else {
            String amount = amountField.getText();
            String diagnosis = diagnosisField.getText();
            String direction = directionForUseField.getText();

            String patient = patientChoiceBox.getValue().toString();
            int patientId = Patient.get(patient);

            String medicine = medicineChoiceBox.getValue().toString();
            int medicineId = Medicine.get(medicine);

            String doctor = doctorChoiceBox.getValue().toString();
            int doctorId = Doctor.get(doctor);

            String startDate = String.valueOf(startDateField.getValue());
            String endDate = String.valueOf(endDateField.getValue());

            if (!orderStatusChoiceBox.isDisabled()) {
                if (startDateField.getValue().isAfter(endDateField.getValue())) {
                    showAlert("Ошибка", "Дата открытия позже даты закрытия");
                }
            }

            int lastId = -1;

            if (insertMode == InsertMode.insert) {
                try {
                    if (!orderStatusChoiceBox.isDisabled()) {
                        orderStatusChoiceBox.getValue().toString();
                    }

                    if(!Objects.equals(startDate, "") && !Objects.equals(endDate, "")) {
                        lastId = dbInit.insertPrescription(patientId, medicineId, doctorId, Integer.valueOf(amount), diagnosis, direction);
                    }

                    if (!orderStatusChoiceBox.isDisabled()) {
                        String orderStatus = orderStatusChoiceBox.getValue().toString();
                        int orderStatusId = OrderStatus.get(orderStatus);
                        dbInit.insertProductionOrder(lastId, orderStatusId, Date.valueOf(startDate), Date.valueOf(endDate));
                    }

                    showAlert("Завершено", "Заказ добавлен в список.");
                }
                catch (NullPointerException e) {
                    showAlert("Ошибка", "Заказ требует указать статус, дату открытия и закрытия");
                }
            }

            listener.changed(name_obser, "", name_obser);
            Stage stage = (Stage) insertButton.getScene().getWindow();
            stage.close();
        }
    }

    public void checkMedicineChoiceBoxOnReadyMed(ComboBox Med) throws SQLException {
        ResultSet setReady = connection.executeQueryAndGetResult("select * from medicine WHERE selling_without_presc = 'Продается'");
        List<Integer> Ready = new ArrayList<>();

        if (setReady != null) {
            while (setReady.next()) {
                Integer idReady = setReady.getInt(1);
                Ready.add(idReady);
            }
        }

        if(Ready.contains(Medicine.get(Med.getValue()))) {
            orderStatusChoiceBox.setDisable(true);
            startDateField.setDisable(true);
            endDateField.setDisable(true);
        }

        else {
            orderStatusChoiceBox.setDisable(false);
            startDateField.setDisable(false);
            endDateField.setDisable(false);
        }
    }

    public void helpButtonTapped() {
        dbInit.showAlert("Подсказка", "Если хотите продать лек-во не требующее рецепта:\n" +
                "1) Выбираете лек-во\n" +
                "2) Указываете кол-во\n" +
                "3) Проставить '-' на остальных позициях или вставить нужные данные при необх-ти ");
    }

    public void learnInformationOnStockButtonTapped() {
        productionOrderPane.setVisible(false);
        patientDoctorPane.setDisable(true);
        learnStockPane.setVisible(true);
        configureLearnWindow();
    }

    public void configureLearnWindow() {
        String windowName = "";
            try {
                loadDataInformationOnStockView();
                informationOnStockView.refresh();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    }

    public void loadDataInformationOnStockView() throws SQLException {
        itemsInformationOnStockView.clear();
        columnsInformationOnStockView.clear();
        columnNamesInformationOnStockView.clear();
        String operation = "SELECT * FROM ready_medicine";

        HashMap columnNamesRecipeMedMap = new HashMap<String,String>(){{
            put("name_medicament", "Изготовляемое лек-во");
            put("name_ingredient", "Ингредиент");
            put("total_amount", "Кол-во для выполнения данного заказа");
            put("verdict", "Вердикт");
        }};

        HashMap columnNamesReadyMedYesMap = new HashMap<String,String>(){{
            put("name_medicament", "Готовое лек-во");
            put("stock_quantity", "Кол-во на складе");
            put("'Хватает'", "Кол-во для выполнения данного заказа");
        }};

        HashMap columnNamesReadyMedNoMap = new HashMap<String,String>(){{
            put("name_medicament", "Готовое лек-во");
            put("stock_quantity", "Кол-во на складе");
            put("'Не хватает'", "Кол-во для выполнения данного заказа");
        }};

        Object itemToUpdate = "test";
        String item = "test";
        if(!medicineChoiceBox.getSelectionModel().isEmpty() && !amountField.getText().isEmpty()) {
            operation = "CALL learn_stock_quantity(" + Medicine.get(medicineChoiceBox.getSelectionModel().getSelectedItem()) + " ," + amountField.getText() + ");";
            System.out.println(operation);
        }
        // many-many tables
        ResultSet set = connection.executeQueryAndGetResult(operation);
        ResultSetMetaData metaData = set.getMetaData();
        int columnSize = set.getMetaData().getColumnCount();

        try {
            for (int i = 1; i <= columnSize; i++) {
                String columnName = "";
                System.out.println(metaData.getColumnName(3));
                if (Objects.equals(metaData.getColumnName(3), "total_amount")) {
                    columnName = (String) columnNamesRecipeMedMap.get(metaData.getColumnName(i));
                }
                else if(Objects.equals(metaData.getColumnName(3), "Хватает")) {
                    columnName = (String) columnNamesReadyMedYesMap.get(metaData.getColumnName(i));
                }
                else if(Objects.equals(metaData.getColumnName(3), "Не хватает")) {
                    columnName = (String) columnNamesReadyMedNoMap.get(metaData.getColumnName(i));
                }
                else {
                    columnName = metaData.getColumnName(i);
                }

                TableColumn<Map, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new MapValueFactory<>(columnName));
                column.setMinWidth(40);
                columnsInformationOnStockView.add(column);
                columnNamesInformationOnStockView.add(columnName);
            }


            informationOnStockView.getColumns().setAll(columnsInformationOnStockView);

            for (int i = 1; set.next(); ++i) {
                Map<String, Object> map = new HashMap<>();
                for (int j = 1; j <= columnSize; j++) {
                    String value = set.getString(j);

                    if (value == null) {
                        value = "";
                    }
                    try {
                        java.util.Date date = formatter.parse(value);
                        value = formatter2.format(date);
                    } catch (ParseException ignore) {
                    }
                    map.put(columnNamesInformationOnStockView.get(j - 1), value);
                }
                itemsInformationOnStockView.add(map);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void cleanWindowButtonTapped() {
        patientDoctorPane.setDisable(false);
        productionOrderPane.setVisible(true);
        learnStockPane.setVisible(false);
    }
}
