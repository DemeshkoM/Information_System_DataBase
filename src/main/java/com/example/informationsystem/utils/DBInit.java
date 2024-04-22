package com.example.informationsystem.utils;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.ProductionOrderInsertController;
import com.example.informationsystem.utils.Connection;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Выполняет запросы на создание, удаление таблиц (в разработке), вставку и обновление данных в таблицах БД.
 * В разработке: триггеры, процедуры, индекс
 *
 * @author Mikhail Demeshko
 */
public class DBInit {
    private static final String[] tableNamesArray = {
            "",
            ""
    };

    private final Connection connection;
    private final List<String> tablesName;

    public DBInit(Connection connection) {
        this.connection = connection;
        tablesName = new LinkedList<>();
        tablesName.addAll(Arrays.asList(tableNamesArray));
    }

    public void clear() {
        dropTables();
    }

    public void init() throws SQLException {
        System.out.println("..creating table..");
        createTables();
        createTriggers();
        createProcedure();
        createViews();
        insertInfo();
        System.out.println("..adding base information..");
        System.out.println("-----database successfully created-----");
    }

    public static int getIdFrom(String item) {
        Integer id = Integer.valueOf(getSubstring(" id=", "id=", item));
        return id.intValue();
    }

    public static int getIdFromIngRec(String item) {
        Integer id = Integer.valueOf(getSubstring(" id_recipe=", "id_recipe=", item));
        return id.intValue();
    }

    public static String getSubstring(String start1, String start2, String item) {
        String start = start1;
        int substringStartIndex = item.indexOf(start);
        if (substringStartIndex < 0) {
            start = start2;
            substringStartIndex = item.indexOf(start);
        }
        int endIndex = item.indexOf(',', substringStartIndex);
        if (endIndex < 0) {
            endIndex = item.indexOf('}', substringStartIndex);
        }

        return item.substring(substringStartIndex + start.length(), endIndex);
    }

    private void execute(List<String> queries) {
        for (String query: queries) {
            try {
                connection.executeQuery(query);
            } catch (SQLIntegrityConstraintViolationException ignored) {
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTriggers() throws SQLException {
        //Place Triggers
        String trigger1 =
                "CREATE TRIGGER check_on_ready_medicine BEFORE INSERT ON production_order " +
                        "FOR EACH ROW " +
                        "        BEGIN " +
                        "DECLARE msg VARCHAR(128); " +

        "if(NEW.id IN (SELECT id FROM prescription " +
                "WHERE medicine_id IN (SELECT id FROM ready_medicine))) THEN " +
                       "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – некорректные данные!'; " +
                        "END if; " +
                        "END; ";


        connection.executeQuery("DROP TRIGGER if exists check_on_ready_medicine; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger1);
        //connection.executeQuery("        delimiter ;");

        String trigger2 = "CREATE TRIGGER insert_sale_of_ready_med " +
        "AFTER INSERT " +
        "ON prescription FOR EACH ROW " +
        "BEGIN " +
        "if(NEW.id IN (SELECT id FROM prescription " +
                "WHERE medicine_id IN (SELECT id FROM ready_medicine))) THEN " +
        "INSERT INTO sales(id, sales_date) " +
       "VALUES(NEW.ID, CURRENT_DATE()); " +
        "END IF; " +
        "END; ";

        connection.executeQuery("DROP TRIGGER if exists insert_sale_of_ready_med; ");
        connection.executeQuery(trigger2);

        String trigger3 = "CREATE TRIGGER insert_info_of_ready_med_without_presc" +
        " BEFORE INSERT" +
        " ON prescription FOR EACH ROW" +
                " BEGIN" +
        " IF(NEW.medicine_id IN (SELECT id FROM medicine WHERE selling_without_presc = 'не продается') AND (NEW.patient_id = 1 OR NEW.doctor_id = 1)) THEN" +
        " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – при продаже лек-ва, для которого нужен рецепт, пациент и доктор должны быть конкретизированы!';" +
        " END IF;" +
        " END;";

        connection.executeQuery("DROP TRIGGER if exists insert_info_of_ready_med_without_presc;");
        connection.executeQuery(trigger3);
    }

    public void createProcedure() throws SQLException {
        //Place Procedures
        String procedure1 = "CREATE PROCEDURE update_stock_ready_med (IN pre_med_id INTEGER, IN amount INTEGER) " +
        "BEGIN " +
        "if EXISTS (SELECT pre_med_id FROM ready_medicine) THEN " +
        "UPDATE ready_medicine " +
        "SET stock_quantity = IF(stock_quantity - amount >= 0, stock_quantity - amount, stock_quantity) " +
        "WHERE id = pre_med_id; " +
        "END IF; " +
        "END;";
        connection.executeQuery("DROP PROCEDURE if exists update_stock_ready_med; ");
        connection.executeQuery(procedure1);

        String procedure2 = "CREATE PROCEDURE update_stock_ingredient (IN pre_med_id INTEGER, IN amount INTEGER)" +
        " BEGIN" +
        " if EXISTS (SELECT pre_med_id FROM ingredient_recipe) THEN" +
        " UPDATE ingredient"+
        " SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id) AND (stock_quantity - amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id AND id_ingredient = id) >= 0)), stock_quantity - amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id AND id_ingredient = id), stock_quantity);" +
        " END IF;" +
        " END;";
        connection.executeQuery("DROP PROCEDURE if exists update_stock_ingredient; ");
        connection.executeQuery(procedure2);
    }

    public void createViews() throws SQLException {
        String view_search_prescription = "CREATE VIEW search_prescription AS" +
        " SELECT pre.id, p.full_name_patient, m.name_medicament, md.name_med_diagnosis_description, d.full_name_doctor, amount, diagnosis, direction_for_use," +
        "        pro.start_date, pro.end_date, s.sales_date, os.name_order_status FROM prescription pre" +
        " INNER JOIN patient p ON(pre.patient_id = p.id)" +
        " INNER JOIN medicine m ON(pre.medicine_id = m.id)" +
        " INNER JOIN doctor d ON(pre.doctor_id = d.id)" +
        " LEFT JOIN production_order pro ON(pre.id = pro.id)" +
        " LEFT JOIN sales s ON(pre.id = s.id)" +
        " LEFT JOIN order_status os ON(pro.status_id = os.id)" +
        " LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id)";
        connection.executeQuery("DROP VIEW if exists search_prescription; ");
        connection.executeQuery(view_search_prescription);

        String view_search_ready_medicine = "CREATE VIEW search_ready_medicine AS" +
        " SELECT r.id, m.name_medicament, m.selling_without_presc, mt.name_med_type, md.name_med_diagnosis_description, r.stock_quantity, r.critical_quantity, r.price FROM ready_medicine r" +
        " INNER JOIN medication_types mt ON(r.medicament_type_id = mt.id)" +
        " INNER JOIN medicine m ON(r.id = m.id)" +
        " LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id)";

        connection.executeQuery("DROP VIEW if exists search_ready_medicine; ");
        connection.executeQuery(view_search_ready_medicine);
    }

    public void createIndex(String tableName, String columnName, String indexName) {
        //Creation of Index
    }

    public void dropTables() {
        dropTable("");
        dropTable("");
    }

    public void dropTable(String tableName) {
        PreparedStatement preparedStatement = null;
        String sqlDropTable = "DROP TABLE " + tableName;
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlDropTable);
            preparedStatement.executeUpdate();
        } catch (SQLException ignored) {
            System.err.println("can't drop " + tableName + " table");
        }
    }

    public void showAlert(String message, String comment) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(comment);
        alert.showAndWait();
    }

    public void createTables() {
        PreparedStatement preparedStatement = null;
        String sqlCreateMedicationCategoryTable = "create TABLE if NOT exists medication_category(" +
                "id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_med_category    varchar(255) not null UNIQUE" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateMedicationCategoryTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create medication_category table");
            throwables.printStackTrace();
        }

        String sqlCreateMedicationTypesTable = "create TABLE if NOT exists medication_types(" +
                "id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_med_type    varchar(255) not null UNIQUE," +
                "name_med_category_id INTEGER NOT NULL," +

        "FOREIGN KEY (name_med_category_id) REFERENCES medication_category(id) ON UPDATE CASCADE ON DELETE CASCADE" +
        ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateMedicationTypesTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create medication_types table");
            throwables.printStackTrace();
        }

        String sqlMedicationDiagnosisDescriptionTable = "create TABLE if NOT exists medication_diagnosis_description(" +
                "id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_med_diagnosis_description varchar(255) not null UNIQUE" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlMedicationDiagnosisDescriptionTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create medication_desc table");
            throwables.printStackTrace();
        }

        String sqlCreateMedicineTable = "create TABLE if NOT exists medicine(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_medicament              varchar(255) not null unique," +
                "medicament_type_id INTEGER not null," +
                "med_diagnosis_description_id INTEGER not null,"+
                "selling_without_presc varchar(255) not null," +

                "FOREIGN KEY (medicament_type_id) REFERENCES medication_types(id) ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY (med_diagnosis_description_id) REFERENCES medication_diagnosis_description(id) ON UPDATE CASCADE ON DELETE CASCADE"+
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateMedicineTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create medicine table");
            throwables.printStackTrace();
        }

        String sqlCreateReadyMedicineTable = "CREATE TABLE if NOT exists ready_medicine(" +
                "id                INT PRIMARY KEY NOT NULL," +
                "medicament_type_id INTEGER not NULL," +
                "stock_quantity	INT NOT NULL," +
                "critical_quantity INT NOT NULL," +
                "price INT NOT NULL," +

                "FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY (medicament_type_id) REFERENCES medicine(medicament_type_id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateReadyMedicineTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create ready_medicine table");
            throwables.printStackTrace();
        }

        String sqlCreateDoctorTable = "CREATE TABLE if NOT exists doctor(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "full_name_doctor VARCHAR(255) NOT NULL" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateDoctorTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create doctor table");
            throwables.printStackTrace();
        }

        String sqlCreatePatientTable = "CREATE TABLE if NOT exists patient(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "full_name_patient VARCHAR(255) NOT NULL," +
                "phone_number VARCHAR(25) NOT NULL," +
                "address VARCHAR(255) NOT NULL," +
                "date_of_birth DATE NOT NULL" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreatePatientTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create patient table");
            throwables.printStackTrace();
        }

        String sqlCreatePrescriptionTable = "CREATE TABLE if NOT exists prescription(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "patient_id INTEGER not null," +
                "medicine_id INTEGER not null," +
                "doctor_id INTEGER not null," +
                "amount INTEGER not null," +
                "diagnosis VARCHAR(255) NOT NULL," +
        "direction_for_use VARCHAR(255) NOT NULL," +

        "FOREIGN KEY (patient_id) REFERENCES patient(id)," +
        "FOREIGN KEY (medicine_id) REFERENCES medicine(id)," +
        "FOREIGN KEY (doctor_id) REFERENCES doctor(id)"+
        ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreatePrescriptionTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create prescription table");
            throwables.printStackTrace();
        }

        String sqlCreateOrderStatusTable = "CREATE TABLE if NOT exists order_status(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_order_status VARCHAR(100) NOT NULL unique"+
        ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateOrderStatusTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create order_status table");
            throwables.printStackTrace();
        }

        String sqlCreateProductionOrderTable = "CREATE TABLE if NOT exists production_order(" +
                "id                INT PRIMARY KEY NOT NULL," +
                "status_id INTEGER not null," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +

                "FOREIGN KEY (status_id) REFERENCES order_status(id) ON UPDATE CASCADE ON DELETE CASCADE,"+
                "FOREIGN KEY (id) REFERENCES prescription(id) ON UPDATE CASCADE ON DELETE CASCADE"+
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateProductionOrderTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create production_order table");
            throwables.printStackTrace();
        }

        String sqlCreateSalesTable = "CREATE TABLE if NOT exists sales("+
                "id                INT PRIMARY KEY NOT NULL,"+
                "sales_date DATE NOT NULL,"+

                "FOREIGN KEY (id) REFERENCES prescription(id) ON UPDATE CASCADE ON DELETE CASCADE"+
        ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateSalesTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create sales table");
            throwables.printStackTrace();
        }

        String sqlCreateRecipeTable = "CREATE TABLE if NOT EXISTS recipe(" +
                "id                INT PRIMARY KEY NOT NULL,"+
                "preparation_method VARCHAR(5000) NOT NULL," +
                "time_hours INT NOT NULL,"+

                "FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateRecipeTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create recipe table");
            throwables.printStackTrace();
        }

        String sqlCreateIngredientTable = "CREATE TABLE if NOT exists ingredient(" +
                "id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_ingredient VARCHAR(255) NOT NULL," +
                "stock_quantity	INT NOT NULL," +
                "critical_quantity INT NOT NULL,"+
                "price INT NOT NULL" +
        ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateIngredientTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create ingredient table");
            throwables.printStackTrace();
        }

        String sqlCreateRecipeIngredientTable = "CREATE TABLE if NOT EXISTS ingredient_recipe("+
                "id_recipe                INT NOT NULL,"+
                "id_ingredient                INT NOT NULL,"+
                "amount_ingredient INT NOT null,"+
                "PRIMARY KEY (id_recipe , id_ingredient)"+
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateRecipeIngredientTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create recipe_ingredient table");
            throwables.printStackTrace();
        }
        // add many-many tables
    }

    public void insertInfo() {

        insertMedicationCategory("Готовое");
        insertMedicationCategory("Изготовляемое");
        insertMedicationCategory("Готовое/Изготовляемое");


        insertMedicationTypes("Мази", 3);
        insertMedicationTypes("Настойки", 3);
        insertMedicationTypes("Таблетки", 1);
        insertMedicationTypes("Микстуры", 2);
        insertMedicationTypes("Порошки", 2);
        insertMedicationTypes("Растворы", 3);

        insertMedicationDiagnosisDescription("Аллергия");
        insertMedicationDiagnosisDescription("Дерматология");
        insertMedicationDiagnosisDescription("ЖКТ");
        insertMedicationDiagnosisDescription("Нервная система");
        insertMedicationDiagnosisDescription("Обезболивающие");
        insertMedicationDiagnosisDescription("Противовирусные");
        insertMedicationDiagnosisDescription("Простуда и грипп");


        insertMedicine("Назол", 6, 7, "не продается");
        insertMedicine("Фестал", 3, 3,"не продается");
        insertMedicine("Гербион сироп подорожника", 4,7, "не продается");

        insertReadyMedicine(1, 6, 100, 25, 100);
        insertReadyMedicine(2, 3, 100, 25,100);

        insertDoctor("-");
        insertDoctor("Петров Борис Викторович");

        insertPatient("-", "-", "-", Date.valueOf("1900-01-01"));
        insertPatient("Иванов Василий Павлович", "+79861236523", "ул. Чуского 1", Date.valueOf("1987-06-12"));

        insertPrescription(2,1,2,1,"Простуда", "Внутрь");
        insertPrescription(2,3,2,1,"Простуда", "Внутрь");

        insertOrderStatus("Ожидание доставки ингредиентов");
        insertOrderStatus("Ожидание оплаты");
        insertOrderStatus("В процессе приготовления");
        insertOrderStatus("Готов");
        insertOrderStatus("Завершен");
        insertOrderStatus("Пропал");

        insertProductionOrder(2,3,Date.valueOf("2024-01-12"), Date.valueOf("2024-01-15"));
        insertSales(2,Date.valueOf("2024-01-12"));

        insertRecipe(3,"Смешать ингредиенты", 1);
        insertIngredient("Подорожник", 100,25,10);
        insertRecipeIngredient(3,1,20);
    }

    public void insertMedicationCategory (String name) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO medication_category(name_med_category) " +
                "VALUES ('" + name + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into medication_category table");
            throwables.printStackTrace();
        }
    }

    public void insertMedicationTypes (String nameMedType, Integer medicationCategoryId) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO medication_types(name_med_type, name_med_category_id) " +
                "VALUES ('" + nameMedType + "', '" + medicationCategoryId + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into medication_types table");
            throwables.printStackTrace();
        }
    }

    public void insertMedicationDiagnosisDescription (String nameMedDesc) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO medication_diagnosis_description(name_med_diagnosis_description) " +
                "VALUES ('" + nameMedDesc + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into medication_types table");
            throwables.printStackTrace();
        }
    }

    public void insertMedicine (String nameMedicament, Integer nameMedicamentTypeId, Integer nameMedDiagDescId, String sellingWithoutPresc) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO medicine(name_medicament, medicament_type_id, med_diagnosis_description_id, selling_without_presc) " +
                "VALUES ('" + nameMedicament + "', '" + nameMedicamentTypeId + "','" +nameMedDiagDescId + "','" + sellingWithoutPresc + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into medicine table");
            throwables.printStackTrace();
        }
    }

    public void insertReadyMedicine (Integer id, Integer nameMedicamentTypeId, Integer stockQuantity, Integer criticalQuantity, Integer price) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO ready_medicine(id, medicament_type_id, stock_quantity, critical_quantity, price) " +
                "VALUES ('" + id + "', '" + nameMedicamentTypeId + "', '" + stockQuantity + "', '" + criticalQuantity + "','" + price + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into ready_medicine table");
            throwables.printStackTrace();
        }
    }

    public void insertDoctor (String name) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO doctor(full_name_doctor) " +
                "VALUES ('" + name + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into doctor table");
            throwables.printStackTrace();
        }
    }

    public void insertPatient (String name, String phoneNumber, String address, Date dateOfBirth) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO patient(full_name_patient, phone_number, address, date_of_birth) " +
                "VALUES ('" + name + "', '" + phoneNumber + "', '" + address + "', '" + dateOfBirth + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into patient table");
            throwables.printStackTrace();
        }
    }

    public Integer insertPrescription (Integer patientId, Integer medicineId, Integer doctorId, Integer amount,
                                    String diagnosis, String directionForUse) {
        PreparedStatement preparedStatement = null;
        Integer lastInsertId = 0;

        String sqlInsertTable = "INSERT INTO prescription(patient_id, medicine_id, doctor_id, amount, diagnosis, direction_for_use) " +
                "VALUES ('" + patientId + "', '" + medicineId + "', '" + doctorId + "', '" + amount + "', '" + diagnosis + "', '" + directionForUse + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet generatedKeyResult = preparedStatement.getGeneratedKeys();

            if (generatedKeyResult.next()) {
                lastInsertId = generatedKeyResult.getInt(1);
            }
        } catch (SQLException throwables) {
            Integer sqlErrorCodeInsertPrescriptionWithoutNames = 1644;
            if (throwables.getErrorCode() == sqlErrorCodeInsertPrescriptionWithoutNames) {
                showAlert("Вставка отменена", "При продаже лек-ва, для которого нужен рецепт, пациент и доктор должны иметь ФИО!");
            }
            System.out.println("can't insert into prescription table");
            throwables.printStackTrace();
        }

        try {
            String updateStock = "CALL update_stock_ready_med(" + medicineId + ", " + amount + ");";
            connection.executeQuery(updateStock);
        } catch (SQLException throwables) {
            System.out.println("can't call procedure update_stock_ready_med");
            throwables.printStackTrace();
        }

        try {
            String updateStock = "CALL update_stock_ingredient(" + medicineId + ", " + amount + ");";
            connection.executeQuery(updateStock);
        } catch (SQLException throwables) {
            System.out.println("can't call procedure update_stock_ingredient");
            throwables.printStackTrace();
        }

        return lastInsertId;
    }

    public void insertOrderStatus (String name) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO order_status(name_order_status) " +
                "VALUES ('" + name + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into order_status table");
            throwables.printStackTrace();
        }
    }

    public void insertProductionOrder (Integer id, Integer statusId, Date startDate, Date endDate) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO production_order(id, status_id, start_date, end_date) " +
                "VALUES ('" + id + "', '" + statusId + "', '" + startDate + "', '" + endDate + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            Integer sqlErrorCodeInsertReadyMed = 1644;
            if (throwables.getErrorCode() == sqlErrorCodeInsertReadyMed) {
                showAlert("Нельзя создавать заказ на лекарства категории 'Готовое'", "");
            }
            System.out.println("can't insert into production_order table");
            throwables.printStackTrace();
        }
    }

    public void insertSales (Integer id, Date salesDate) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO sales(id, sales_date)" +
                "VALUES ('" + id + "', '" + salesDate + "') ON DUPLICATE KEY UPDATE sales_date='" + salesDate + "'";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into sales table");
            throwables.printStackTrace();
        }
    }

    public void insertRecipe (Integer id, String preparationMethod, Integer timeHours) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO recipe(id, preparation_method, time_hours)" +
                "VALUES ('" + id + "', '" + preparationMethod + "','"+ timeHours + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into recipe table");
            throwables.printStackTrace();
        }
    }

    public void insertIngredient (String nameIngredient, Integer stockQuantity, Integer criticalQuantity, Integer price) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO ingredient(name_ingredient, stock_quantity, critical_quantity, price) " +
                "VALUES ('" + nameIngredient + "', '" + stockQuantity + "', '" + criticalQuantity + "','" + price + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into ingredient table");
            throwables.printStackTrace();
        }
    }

    public void insertRecipeIngredient (Integer RecipeId, Integer IngredientId, Integer amountIngredient) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO ingredient_recipe(id_recipe, id_ingredient, amount_ingredient) " +
                "VALUES ('" + RecipeId + "', '" + IngredientId + "', '" + amountIngredient +"')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into recipe_ingredient table");
            throwables.printStackTrace();
        }
    }

    public void updateMedicationCategory(int id, String name) {
        String sql = "UPDATE medication_category SET " +
                " name_med_category = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateMedicationTypes(int id, String name, Integer medicationCategoryId) {
        String sql = "UPDATE medication_types SET " +
                " name_med_type = '" + name + "'," +
                " name_med_category_id = " + medicationCategoryId + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateMedicationDiagnosisDescription(int id, String name) {
        String sql = "UPDATE medication_diagnosis_description SET " +
                " name_med_type = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateMedicine(int id, String name, Integer medicationTypeId, Integer medicationDescId, String sellingWithoutPresc) {
        String sql = "UPDATE medicine SET " +
                " name_medicament = '" + name + "'," +
                " medicament_type_id = " + medicationTypeId + "," +
                " med_diagnosis_description_id =" + medicationDescId + "," +
                " selling_without_presc = '" + sellingWithoutPresc + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateReadyMedicine(int id, Integer medicationTypeId, Integer stockQuantity, Integer criticalQuantity, Integer price) {

        String sql = "UPDATE ready_medicine SET " +
                " medicament_type_id = " + medicationTypeId + "," +
                " stock_quantity = " + stockQuantity + "," +
                " critical_quantity = " + criticalQuantity + "," +
                " price= " + price + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }

    public void updateDoctor(int id, String name) {
        String sql = "UPDATE doctor SET " +
                " full_name_doctor = '" + name + "'" +
                " WHERE id = " + id;
        System.out.println(sql);
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updatePatient(int id, String name, String phoneNumber, String address, String dateOfBirth) {
        String sql = "UPDATE patient SET " +
                " full_name_patient = '" + name + "'," +
                " phone_number = '" + phoneNumber + "'," +
                " address = '" + address + "'," +
                " date_of_birth = '" + dateOfBirth + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updatePrescription(int id, Integer patientId, Integer medicineId, Integer doctorId, Integer amount,
                                   String diagnosis, String directionForUse) {
        String sql = "UPDATE prescription SET " +
                " patient_id = '" + patientId + "'," +
                " medicine_id = '" + medicineId + "'," +
                " doctor_id = '" + doctorId + "'," +
                " amount = " + amount + "," +
                " diagnosis = '" + diagnosis + "'," +
                " direction_for_use = '" + directionForUse + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateSecondAttributesPrescription(int id, String diagnosis, String directionForUse) {
        String sql = "UPDATE prescription SET " +
                " diagnosis = '" + diagnosis + "'," +
                " direction_for_use = '" + directionForUse + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateOrderStatus(int id, String name) {
        String sql = "UPDATE order_status SET " +
                " name_order_status = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateProductionOrder(int id, Integer statusId, Date startDate, Date endDate) {
        String sql = "UPDATE production_order SET " +
                " status_id = '" + statusId + "'," +
                " start_date = '" + startDate + "'," +
                " end_date = '" + endDate + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateProductionOrderEndDate(int id, Date endDate) {
        String sql = "UPDATE production_order SET " +
                " end_date = '" + endDate + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateProductionOrderStatus(int id, int statusId) {
        String sql = "UPDATE production_order SET " +
                " status_id = '" + statusId + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateSales(int id, Date salesDate) {
        String sql = "UPDATE sales SET " +
                " sales_date = '" + salesDate + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateRecipe(int id, String preparationMethod, Integer timeHours) {
        String sql = "UPDATE if EXISTS recipe SET " +
                " preparation_method = '" + preparationMethod + "'," +
                " time_hours =" + timeHours + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateIngredient(int id, String nameIngredient, Integer stockQuantity, Integer criticalQuantity, Integer price) {

        String sql = "UPDATE ingredient SET " +
                " name_ingredient = '" + nameIngredient + "'," +
                " stock_quantity = " + stockQuantity + "," +
                " critical_quantity = " + criticalQuantity + "," +
                " price= " + price + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }

    public void updateRecipeIngredient(int recipeId, int ingredientId, Integer amount) {

        String sql = "UPDATE ingredient_recipe SET " +
                " amount_ingredient= " + amount + "" +
                " WHERE id_recipe = " + recipeId + " AND id_ingredient=" + ingredientId + "";
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }
}
