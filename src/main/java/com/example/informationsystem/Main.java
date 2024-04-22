/*
СОЗДАНИЕ ТАБЛИЦ
ВСТАВКУ ПОКАЗАТЕЛЬНЫХ ДАННЫХ В ТАБЛИЦЫ МОЖНО ОСУЩЕСТВИТЬ ЧЕРЕЗ
ЗАПУСК ИСПОЛНЯЕМОГО ФАЙЛА->ВЫБОР РОЛИ АДМИН->ПЕРЕЙТИ К БАЗЕ ДАННЫХ->СОЗДАТЬ БД

SQL-ЗАПРОСЫ НА ВСТАВКУ ПОКАЗАТЕЛЬНЫХ ДАННЫХ ДОБАВЯТСЯ СЮДА ПОЗЖЕ

create TABLE if NOT exists medication_category(
        id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name_med_category    varchar(255) not null UNIQUE,
        );

        create TABLE if NOT exists medication_types(
        id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name_med_type    varchar(255) not null UNIQUE,
        name_med_category_id INTEGER NOT NULL,

        FOREIGN KEY (name_med_category_id) REFERENCES medication_category(id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT EXISTS medication_diagnosis_description(
        id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name_med_diagnosis_description varchar(255) not null UNIQUE
        );

        create TABLE if NOT exists medicine(
        id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name_medicament              varchar(255) not null unique,
        medicament_type_id INTEGER not null,
        med_diagnosis_description_id INTEGER not null,
        selling_without_presc varchar(255) not null,


        FOREIGN KEY (medicament_type_id) REFERENCES medication_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
        FOREIGN KEY (med_diagnosis_description_id) REFERENCES medication_diagnosis_description(id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT exists ready_medicine(
        id                INT PRIMARY KEY NOT NULL,
        medicament_type_id INTEGER not NULL,
        stock_quantity	INT NOT NULL,
        critical_quantity INT NOT NULL,
        price INT NOT NULL,

        FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE,
        FOREIGN KEY (medicament_type_id) REFERENCES medicine(medicament_type_id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT exists doctor(
        id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        full_name_doctor VARCHAR(255) NOT NULL
        );

        CREATE TABLE if NOT exists patient(
        id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        full_name_patient VARCHAR(255) NOT NULL,
        phone_number VARCHAR(25) NOT NULL,
        address VARCHAR(255) NOT NULL,
        date_of_birth DATE NOT NULL
        );

        CREATE TABLE if NOT exists prescription(
        id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        patient_id INTEGER not null,
        medicine_id INTEGER not null,
        doctor_id INTEGER not null,
        amount INTEGER not null,
        diagnosis VARCHAR(255) NOT NULL,
        direction_for_use VARCHAR(255) NOT NULL,

        FOREIGN KEY (patient_id) REFERENCES patient(id),
        FOREIGN KEY (medicine_id) REFERENCES medicine(id),
        FOREIGN KEY (doctor_id) REFERENCES doctor(id)
        );

        CREATE TABLE if NOT exists order_status(
        id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name_order_status VARCHAR(100) NOT NULL unique
        );

        CREATE TABLE if NOT exists production_order(
        id                INT PRIMARY KEY NOT NULL,
        status_id INTEGER not null,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,

        FOREIGN KEY (status_id) REFERENCES order_status(id) ON UPDATE CASCADE ON DELETE CASCADE,
        FOREIGN KEY (id) REFERENCES prescription(id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT exists sales(
        id                INT PRIMARY KEY NOT NULL,
        sales_date DATE NOT NULL,

        FOREIGN KEY (id) REFERENCES prescription(id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT EXISTS recipe(
        id                INT PRIMARY KEY NOT NULL,
        preparation_method VARCHAR(5000) NOT NULL,
        time_hours INT NOT NULL,

        FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE
        );

        CREATE TABLE if NOT exists ingredient(
        id                INT PRIMARY KEY NOT NULL,
        name_ingredient VARCHAR(255) NOT NULL,
        stock_quantity	INT NOT NULL,
        critical_quantity INT NOT NULL,
        price INT NOT NULL
        );

        CREATE TABLE if NOT EXISTS ingredient_recipe(
        id_recipe                INT PRIMARY KEY NOT NULL,
        id_ingredient                INT PRIMARY KEY NOT NULL,
        amount_ingredient INT NOT null
        );




--------------------------------------
ТРИГГЕРЫ
--------------------------------------

DROP TRIGGER if exists check_on_ready_medicine;

delimiter //

CREATE TRIGGER check_on_ready_medicine BEFORE INSERT ON production_order
FOR EACH ROW
BEGIN
	DECLARE msg VARCHAR(128);

	if(NEW.id IN (SELECT id FROM prescription
		WHERE medicine_id IN (SELECT id FROM ready_medicine))) THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – некорректные данные!';
	END if;
END;//

delimiter ;

DROP TRIGGER if exists insert_sale_of_ready_med;

        DELIMITER //

        CREATE TRIGGER insert_sale_of_ready_med
        AFTER INSERT
        ON prescription FOR EACH ROW
        BEGIN
        if(NEW.id IN (SELECT id FROM prescription
        WHERE medicine_id IN (SELECT id FROM ready_medicine))) THEN
        INSERT INTO sales(id, sales_date)
        VALUES(NEW.ID, CURRENT_DATE());
        END IF;

        END;//

        DELIMITER ;

        DROP TRIGGER if exists insert_info_of_ready_med_without_presc;

        DELIMITER //

        CREATE TRIGGER insert_info_of_ready_med_without_presc
        BEFORE INSERT
        ON prescription FOR EACH ROW
        BEGIN
        IF(NEW.medicine_id IN (SELECT id FROM medicine WHERE selling_without_presc = 'не продается') AND (NEW.patient_id = 1 OR NEW.doctor_id = 1)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – при продаже лек-ва, для которого нужен рецепт, пациент и доктор должны быть конкретизированы!';
        END IF;
        END;//

        DELIMITER ;


--------------------------------------
ПРОЦЕДУРЫ
--------------------------------------

DROP PROCEDURE IF EXISTS update_stock_ready_med;

delimiter //
CREATE PROCEDURE update_stock_ready_med (IN pre_med_id INTEGER, IN amount INTEGER)
       BEGIN
       if EXISTS (SELECT pre_med_id FROM ready_medicine) THEN
      	UPDATE ready_medicine
      	SET stock_quantity = IF(stock_quantity - amount >= 0, stock_quantity - amount, stock_quantity)
      	WHERE id = pre_med_id;
       END IF;
       END;//
delimiter ;

DROP PROCEDURE IF EXISTS update_stock_ingredient;

delimiter //
CREATE PROCEDURE update_stock_ingredient (IN pre_med_id INTEGER, IN amount INTEGER)
       BEGIN
       if EXISTS (SELECT pre_med_id FROM ingredient_recipe) THEN
      	UPDATE ingredient
      	SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id) AND (stock_quantity - amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id AND id_ingredient = id) >= 0)), stock_quantity - amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = pre_med_id AND id_ingredient = id), stock_quantity);
       END IF;
       END;//
delimiter ;

CALL update_stock_ingredient(3,4);


--------------------------------------
ПРЕДСТАВЛЕНИЯ
--------------------------------------

CREATE VIEW search_prescription AS
SELECT pre.id, p.full_name_patient, m.name_medicament, md.name_med_diagnosis_description, d.full_name_doctor, amount, diagnosis, direction_for_use,
pro.start_date, pro.end_date, s.sales_date, os.name_order_status FROM prescription pre
INNER JOIN patient p ON(pre.patient_id = p.id)
INNER JOIN medicine m ON(pre.medicine_id = m.id)
INNER JOIN doctor d ON(pre.doctor_id = d.id)
LEFT JOIN production_order pro ON(pre.id = pro.id)
LEFT JOIN sales s ON(pre.id = s.id)
LEFT JOIN order_status os ON(pro.status_id = os.id)
LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id);



CREATE VIEW search_ready_medicine AS
SELECT r.id, m.name_medicament, m.selling_without_presc, mt.name_med_type, md.name_med_diagnosis_description, r.stock_quantity, r.critical_quantity, r.price FROM ready_medicine r
INNER JOIN medication_types mt ON(r.medicament_type_id = mt.id)
INNER JOIN medicine m ON(r.id = m.id)
LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id);
*/




package com.example.informationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.informationsystem.controllers.base.EntranceController;
import com.example.informationsystem.utils.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class Main extends Application {
    private static final Connection connection = new Connection();

    public static void main (String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("INFORMATION SYSTEM");
        Locale.setDefault(new Locale("ru", "RU"));
        InputStream inputStream = getClass().getResourceAsStream("/com/example/informationsystem/windows/entrance_window.fxml");
        Parent root = new FXMLLoader().load(inputStream);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        connection.close();
        super.stop();
    }

    public static Connection getConnection() {
        return connection;
    }
}