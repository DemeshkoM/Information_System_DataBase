/*
СОЗДАНИЕ ТАБЛИЦ
ВСТАВКУ ПОКАЗАТЕЛЬНЫХ ДАННЫХ В ТАБЛИЦЫ МОЖНО ОСУЩЕСТВИТЬ ЧЕРЕЗ
ЗАПУСК ИСПОЛНЯЕМОГО ФАЙЛА->ВЫБОР РОЛИ АДМИН->ПЕРЕЙТИ К БАЗЕ ДАННЫХ->СОЗДАТЬ БД

CREATE DATABASE IF NOT EXISTS DBname;

USE DBname;

create TABLE if NOT exists medication_category(
    id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name_med_category    varchar(255) not null UNIQUE
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
    selling_without_presc varchar(255) not NULL,

    FOREIGN KEY (medicament_type_id) REFERENCES medication_types(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (med_diagnosis_description_id) REFERENCES medication_diagnosis_description(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE if NOT exists ready_medicine(
	 id                INT PRIMARY KEY NOT NULL,
    stock_quantity	INT NOT NULL,
	 critical_quantity INT NOT NULL,
	 price INT NOT NULL,

    FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE
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
	 id                INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name_ingredient VARCHAR(255) NOT NULL,
    stock_quantity	INT NOT NULL,
	 critical_quantity INT NOT NULL,
	 price INT NOT NULL
);

CREATE TABLE if NOT EXISTS ingredient_recipe(
	id_recipe                INT NOT NULL,
	id_ingredient                INT NOT NULL,
	amount_ingredient INT NOT NULL,

	PRIMARY KEY (id_recipe , id_ingredient),
	FOREIGN KEY (id_recipe) REFERENCES recipe(id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (id_ingredient) REFERENCES ingredient(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE  if NOT exists ready_stock(
	 id                INT PRIMARY KEY NOT NULL,
    stock_quantity	INT NOT NULL,

	FOREIGN KEY (id) REFERENCES ready_medicine(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE  if NOT exists ingredient_stock(
	 id                INT PRIMARY KEY NOT NULL,
    name_ingredient VARCHAR(255) NOT NULL,
    stock_quantity	INT NOT NULL,

	FOREIGN KEY (id) REFERENCES ingredient(id) ON UPDATE CASCADE ON DELETE CASCADE
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
		WHERE medicine_id IN (SELECT id FROM search_ready_medicine WHERE selling_without_presc = 'продается'))) THEN
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
        if(NEW.medicine_id IN (SELECT id FROM search_ready_medicine WHERE selling_without_presc = 'продается')) THEN
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

        DROP TRIGGER if exists check_on_stock_quantity_update_ready_med;

        delimiter //

        CREATE TRIGGER check_on_stock_quantity_update_ready_med BEFORE UPDATE ON ready_medicine
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);
        DECLARE cur_role VARCHAR(128);

        SELECT USER() INTO cur_role;

        if(OLD.stock_quantity > NEW.stock_quantity AND (cur_role LIKE '%provider%')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Нельзя уменьшать кол-во лекарства на складе без продажи оного!';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists check_on_stock_quantity_update_ingredient;

        delimiter //

        CREATE TRIGGER check_on_stock_quantity_update_ingredient BEFORE UPDATE ON ingredient
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);
        DECLARE cur_role VARCHAR(128);
        DECLARE test_p VARCHAR(128);

        SELECT USER() INTO cur_role;

        if(OLD.stock_quantity > NEW.stock_quantity AND (cur_role LIKE '%provider%')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ТРУП';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists insert_medicine_selling_presc;

        delimiter //

        CREATE TRIGGER insert_medicine_selling_presc BEFORE INSERT ON medicine
        FOR EACH ROW
        BEGIN
        if(NEW.selling_without_presc = 'продается' AND NEW.medicament_type_id IN (SELECT id FROM medication_types WHERE name_med_category_id = 2)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Лек-ва категории Изготовляемое требуют наличие рецепта при продаже';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists delete_medicine_in_presc;

        delimiter //

        CREATE TRIGGER delete_medicine_in_presc BEFORE DELETE ON medicine
        FOR EACH ROW
        BEGIN
        if(OLD.id IN (SELECT medicine_id FROM prescription)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Лек-во уже было использовано при оформлении заказа. Удаление запрещено';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists delete_patient_in_presc;

        delimiter //

        CREATE TRIGGER delete_patient_in_presc BEFORE DELETE ON patient
        FOR EACH ROW
        BEGIN
        if(OLD.id IN (SELECT patient_id FROM prescription)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Id Пациента уже было использовано при оформлении заказа. Удаление запрещено';
        END if;
        if(OLD.full_name_patient = '-') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Неопределенный ID нужен для продажи лек-в, не требующих рецепта. Удаление запрещено';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists delete_doctor_in_presc;

        delimiter //

        CREATE TRIGGER delete_doctor_in_presc BEFORE DELETE ON doctor
        FOR EACH ROW
        BEGIN
        if(OLD.id IN (SELECT doctor_id FROM prescription)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Id Доктора уже было использовано при оформлении заказа. Удаление запрещено';
        END if;
        if(OLD.full_name_doctor = '-') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Неопределенный ID нужен для продажи лек-в, не требующих рецепта. Удаление запрещено';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists delete_ingredient_in_recipe;

        delimiter //

        CREATE TRIGGER delete_ingredient_in_recipe BEFORE DELETE ON ingredient
        FOR EACH ROW
        BEGIN
        if(OLD.id IN (SELECT id_ingredient FROM ingredient_recipe)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ингредиент используется в каком-то рецепте. Удаление запрещено';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists update_end_date_in_prescription;

        delimiter //

        CREATE TRIGGER update_end_date_in_prescription BEFORE UPDATE ON production_order
        FOR EACH ROW
        BEGIN
        DECLARE sales_date_pro DATE;

        SELECT sales_date INTO sales_date_pro FROM sales WHERE id = OLD.id;

        if(NEW.end_date < OLD.start_date OR NEW.end_date < sales_date_pro) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата закрытия заказа не может быть ранее чем открытия или оплаты';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists insert_sales_date_in_prescription;

        delimiter //

        CREATE TRIGGER insert_sales_date_in_prescription BEFORE INSERT ON sales
        FOR EACH ROW
        BEGIN
        DECLARE start_date_pro DATE;
        DECLARE end_date_pro DATE;

        IF(NEW.id IN(SELECT id FROM production_order)) THEN
        SELECT start_date INTO start_date_pro FROM production_order WHERE id = NEW.id;

        if(NEW.sales_date < start_date_pro) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата оплаты заказа не может быть ранее чем открытия';
        END if;

        SELECT end_date INTO start_date_pro FROM production_order WHERE id = NEW.id;

        if(NEW.sales_date > start_date_pro) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата оплаты заказа не может быть позднее чем закрытия';
        END if;
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists check_on_order_status_insert;

        delimiter //

        CREATE TRIGGER check_on_order_status_insert BEFORE INSERT ON production_order
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);

        if(NEW.status_id <> 1 AND NEW.status_id <> 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – некорректные данные!';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists check_on_order_status_ingredient_ready_is_enough;

        delimiter //

        CREATE TRIGGER check_on_order_status_ingredient_ready_is_enough BEFORE INSERT ON production_order
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);

        DECLARE n INT DEFAULT 0;
        DECLARE i INT DEFAULT 0;
        DECLARE id_ing INT DEFAULT 0;
        DECLARE stock_quantity_ing INT DEFAULT 0;
        DECLARE check_on_enough INT;
        DECLARE medicine_id_pro INT;
        DECLARE amount_pro INT;
        DECLARE stock_quantity_pro INT DEFAULT 0;

        SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;
        SELECT amount INTO amount_pro FROM prescription WHERE id = NEW.id;


        SET check_on_enough = 1;

        if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN
        SELECT COUNT(*) FROM ingredient INTO n;
        SET i=0;
        WHILE i<n DO
        SELECT id INTO id_ing FROM ingredient LIMIT i,1;
        SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;

        IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN
        SET check_on_enough = 0;
        END IF;

        SET i = i + 1;
        END WHILE;

        IF (check_on_enough = 0) THEN
        SET NEW.status_id = 1;
        END IF;
        END if;

        if NEW.status_id = 1 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN
        SELECT COUNT(*) FROM ingredient INTO n;
        SET i=0;
        WHILE i<n DO
        SELECT id INTO id_ing FROM ingredient LIMIT i,1;
        SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;

        IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN
        SET check_on_enough = 0;
        END IF;

        SET i = i + 1;
        END WHILE;

        IF (check_on_enough = 1) THEN
        SET NEW.status_id = 2;
        END IF;
        END if;

        if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN
        SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;

        IF(stock_quantity_pro - amount_pro < 0) THEN
        SET check_on_enough = 0;
        END IF;

        IF (check_on_enough = 0) THEN
        SET NEW.status_id = 1;
        END IF;
        END if;

        if NEW.status_id = 1 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN
        SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;

        IF(stock_quantity_pro - amount_pro < 0) THEN
        SET check_on_enough = 0;
        END IF;

        IF (check_on_enough = 1) THEN
        SET NEW.status_id = 2;
        END IF;
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists check_on_order_status_ingredient_ready_sales_is_ready;

        delimiter //

        CREATE TRIGGER check_on_order_status_ingredient_ready_sales_is_ready BEFORE UPDATE ON production_order
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);

        DECLARE n INT DEFAULT 0;
        DECLARE i INT DEFAULT 0;
        DECLARE id_ing INT DEFAULT 0;
        DECLARE stock_quantity_ing INT DEFAULT 0;
        DECLARE check_on_enough INT;
        DECLARE medicine_id_pro INT;
        DECLARE amount_pro INT;
        DECLARE stock_quantity_pro INT DEFAULT 0;

        SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;
        SELECT amount INTO amount_pro FROM prescription WHERE id = NEW.id;



        SET check_on_enough = 1;

        if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN
        SELECT COUNT(*) FROM ingredient INTO n;
        SET i=0;
        WHILE i<n DO
        SELECT id INTO id_ing FROM ingredient LIMIT i,1;
        SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;

        IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN
        SET check_on_enough = 0;
        END IF;

        SET i = i + 1;
        END WHILE;

        IF (check_on_enough = 0) THEN
        SET NEW.status_id = 1;
        END IF;

        IF (check_on_enough = 1) THEN
        UPDATE ingredient_stock
        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =medicine_id_pro AND id_ingredient = id) >= 0)), stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id), stock_quantity);

        UPDATE ingredient
        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =medicine_id_pro AND id_ingredient = id) >= 0)), stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id), stock_quantity);
        END if;
        END if;

        if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN
        SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;

        IF(stock_quantity_pro - amount_pro < 0) THEN
        SET check_on_enough = 0;
        END IF;

        IF (check_on_enough = 0) THEN
        SET NEW.status_id = 1;
        END IF;

        IF (check_on_enough = 1) THEN
        UPDATE ready_stock
        SET stock_quantity = stock_quantity_pro - amount_pro
        WHERE id = medicine_id_pro;

        UPDATE ready_medicine
        SET stock_quantity = stock_quantity_pro - amount_pro
        WHERE id = medicine_id_pro;
        END if;
        END if;

        if NEW.status_id = 3 AND NEW.id NOT IN (SELECT id FROM sales) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Заказ ещё не оплачен. Для этого надо определить дату.';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists check_on_order_status_update;

        delimiter //

        CREATE TRIGGER check_on_order_status_update BEFORE UPDATE ON production_order
        FOR EACH ROW
        BEGIN
        DECLARE msg VARCHAR(128);
        DECLARE medicine_id_pro INT DEFAULT 0;

        SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;

        if(OLD.status_id = 1 AND NEW.status_id <> 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Ожидание оплаты';
        END if;
        if(OLD.status_id = 2 AND NEW.status_id <> 3 AND medicine_id_pro IN (SELECT id FROM recipe)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет В процессе приготовления';
        END if;
        if(OLD.status_id = 3 AND NEW.status_id <> 4 AND medicine_id_pro IN (SELECT id FROM recipe)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Готов';
        END if;
        if(OLD.status_id = 4 AND (NEW.status_id <> 5 AND NEW.status_id <> 6)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Завершен или Пропал';
        END if;


        if(OLD.status_id = 2 AND NEW.status_id <> 4 AND medicine_id_pro NOT IN (SELECT id FROM recipe)) THEN
        SET NEW.status_id = 4;
        END if;
        if(OLD.status_id = 3 AND NEW.status_id <> 4 AND medicine_id_pro NOT IN (SELECT id FROM recipe)) THEN
        SET NEW.status_id = 4;
        END if;

        if(OLD.status_id = 5 AND NEW.status_id <> 5) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Этот статус конечный';
        END if;
        if(OLD.status_id = 6 AND NEW.status_id <> 6) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Этот статус конечный';
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists insert_ready_stock;

        delimiter //

        CREATE TRIGGER insert_ready_stock AFTER INSERT ON ready_medicine
        FOR EACH ROW
        BEGIN
        INSERT INTO ready_stock (id, stock_quantity) VALUES (NEW.id, NEW.stock_quantity);
        END;//

        delimiter ;

        DROP TRIGGER if exists update_ready_stock_after_presc;

        delimiter //

        CREATE TRIGGER update_ready_stock_after_presc BEFORE INSERT ON prescription
        FOR EACH ROW
        BEGIN
        DECLARE n INT DEFAULT 0;
        DECLARE i INT DEFAULT 0;
        DECLARE id_ing INT DEFAULT 0;
        DECLARE stock_quantity_pro INT DEFAULT 0;
        DECLARE check_on_enough INT;

        SET check_on_enough = 1;

        if NEW.medicine_id IN (SELECT id FROM ready_medicine) THEN
        SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = NEW.medicine_id;

        IF(stock_quantity_pro - NEW.amount < 0) THEN
        SET check_on_enough = 0;
        END IF;
        END if;

        IF (check_on_enough = 1) THEN
        UPDATE ready_stock
        SET stock_quantity = stock_quantity_pro - NEW.amount
        WHERE id = NEW.medicine_id;

        UPDATE ready_medicine
        SET stock_quantity = stock_quantity_pro - NEW.amount
        WHERE id = NEW.medicine_id;
        END IF;
        END;//

        delimiter ;

        DROP TRIGGER if exists update_ready_stock;

        delimiter //

        CREATE TRIGGER update_ready_stock BEFORE UPDATE ON ready_medicine
        FOR EACH ROW
        BEGIN
        DECLARE stock_quantity_pro INT DEFAULT 0;

        SELECT stock_quantity INTO stock_quantity_pro FROM ready_stock WHERE id = NEW.id;

        IF(NEW.stock_quantity >= stock_quantity_pro) THEN
        UPDATE ready_stock
        SET stock_quantity = NEW.stock_quantity
        WHERE id = NEW.id;
        END IF;

        IF(NEW.stock_quantity < stock_quantity_pro) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Запрещено уменьшать кол-во лек-ва со склада без оформления заказа, где он используется';
        END IF;
        END;//

        delimiter ;

        DROP TRIGGER if exists insert_ingredient_stock;

        delimiter //

        CREATE TRIGGER insert_ingredient_stock AFTER INSERT ON ingredient
        FOR EACH ROW
        BEGIN
        INSERT INTO ingredient_stock (id, name_ingredient, stock_quantity) VALUES (NEW.id, NEW.name_ingredient, NEW.stock_quantity);
        END;//

        delimiter ;

        DROP TRIGGER if exists update_ingredient_stock_after_presc;

        delimiter //

        CREATE TRIGGER update_ingredient_stock_after_presc BEFORE INSERT ON prescription
        FOR EACH ROW
        BEGIN
        DECLARE n INT DEFAULT 0;
        DECLARE i INT DEFAULT 0;
        DECLARE id_ing INT DEFAULT 0;
        DECLARE stock_quantity_ing INT DEFAULT 0;
        DECLARE check_on_enough INT;

        SET check_on_enough = 1;

        if NEW.medicine_id IN (SELECT id_recipe FROM ingredient_recipe) THEN
        SELECT COUNT(*) FROM ingredient INTO n;
        SET i=0;
        WHILE i<n DO
        SELECT id INTO id_ing FROM ingredient LIMIT i,1;
        SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;

        IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity_ing - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id_ing) < 0))) THEN
        SET check_on_enough = 0;
        END IF;

        SET i = i + 1;
        END WHILE;

        IF (check_on_enough = 1) THEN
        UPDATE ingredient_stock
        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =NEW.medicine_id AND id_ingredient = id) >= 0)), stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id), stock_quantity);

        UPDATE ingredient
        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =NEW.medicine_id AND id_ingredient = id) >= 0)), stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id), stock_quantity);
        END IF;
        END if;
        END;//

        delimiter ;

        DROP TRIGGER if exists update_ingredient_stock;

        delimiter //

        CREATE TRIGGER update_ingredient_stock BEFORE UPDATE ON ingredient
        FOR EACH ROW
        BEGIN
        DECLARE stock_quantity_pro INT DEFAULT 0;

        SELECT stock_quantity INTO stock_quantity_pro FROM ingredient_stock WHERE id = NEW.id;

        IF(NEW.stock_quantity >= stock_quantity_pro) THEN
        UPDATE ingredient_stock
        SET stock_quantity = NEW.stock_quantity
        WHERE id = NEW.id;
        END IF;

        IF(NEW.stock_quantity < stock_quantity_pro) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Запрещено уменьшать кол-во ингредиента со склада без оформления заказа, где он используется';
        END IF;
        END;//

        delimiter ;


--------------------------------------
ПРОЦЕДУРЫ
--------------------------------------

DROP PROCEDURE IF EXISTS learn_stock_quantity;
delimiter //
CREATE PROCEDURE learn_stock_quantity (IN pre_med_id INTEGER, IN amount INTEGER)
       BEGIN

       DECLARE n INT DEFAULT 0;
		 DECLARE i INT DEFAULT 0;
		 DECLARE id_ing INT DEFAULT 0;
		 DECLARE amount_ing INT DEFAULT 0;
		 DECLARE stock_quantity_ing INT DEFAULT 0;


       if (pre_med_id IN (SELECT id_recipe FROM ingredient_recipe)) THEN

       	DROP TEMPORARY TABLE if exists conclusion;

       	CREATE TEMPORARY TABLE if NOT EXISTS conclusion(
				id_recipe                INT NOT NULL,
				id_ingredient                INT NOT NULL,
				total_amount INT NOT NULL,
				verdict VARCHAR(128) NOT NULL,

				PRIMARY KEY (id_recipe , id_ingredient)
			);

			SELECT COUNT(*) FROM ingredient_recipe WHERE id_recipe = pre_med_id INTO n;
			SET i=0;
			WHILE i<n DO
				SELECT id_ingredient, amount_ingredient*amount INTO id_ing, amount_ing FROM ingredient_recipe WHERE id_recipe = pre_med_id LIMIT i,1;
				SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;

				if(stock_quantity_ing >= amount_ing) THEN
				INSERT INTO conclusion(id_recipe, id_ingredient, total_amount, verdict)
				VALUES(pre_med_id,id_ing,amount_ing,'Хватает');
				END if;

				if(stock_quantity_ing < amount_ing) THEN
				INSERT INTO conclusion(id_recipe, id_ingredient, total_amount, verdict)
				VALUES(pre_med_id,id_ing,amount_ing,'Не хватает');
				END if;

				SET i = i + 1;
			END WHILE;

			SELECT name_medicament AS recipe_medicament, name_ingredient, total_amount, verdict FROM conclusion
			INNER JOIN medicine ON (medicine.id = id_recipe)
			INNER JOIN ingredient ON (ingredient.id = id_ingredient)
			WHERE id_recipe = pre_med_id;

			DROP TEMPORARY TABLE conclusion;
  		 END IF;

  		 if (pre_med_id IN (SELECT id FROM ready_medicine)) THEN
  		 	SELECT stock_quantity INTO stock_quantity_ing FROM ready_medicine WHERE id = pre_med_id;

			if(stock_quantity_ing >= amount) THEN
				SELECT name_medicament, stock_quantity, 'Хватает' FROM ready_medicine
				INNER JOIN medicine ON (medicine.id = ready_medicine.id)
				WHERE ready_medicine.id = pre_med_id;
			END if;

			if(stock_quantity_ing < amount) THEN
				SELECT name_medicament, stock_quantity, 'Не хватает' FROM ready_medicine
				INNER JOIN medicine ON (medicine.id = ready_medicine.id)
				WHERE ready_medicine.id = pre_med_id;
			END if;

  		 END IF;
       END;//
delimiter ;

DROP PROCEDURE IF EXISTS add_Admin;
DELIMITER //

CREATE PROCEDURE add_Admin (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))
BEGIN
DECLARE user_name VARCHAR(255);
DECLARE word VARCHAR(255);


DROP ROLE IF EXISTS 'pharmacy_db_admin';

CREATE ROLE IF NOT EXISTS 'pharmacy_db_admin';

GRANT CREATE USER ON *.* TO 'pharmacy_db_admin';

GRANT RELOAD ON *.* TO 'pharmacy_db_admin';
GRANT CREATE ROUTINE ON dbname.* TO 'pharmacy_db_admin';

GRANT EXECUTE ON PROCEDURE dbname.learn_stock_quantity TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT EXECUTE ON PROCEDURE dbname.add_Cashier TO 'pharmacy_db_admin';
GRANT EXECUTE ON PROCEDURE dbname.add_Producer TO 'pharmacy_db_admin';
GRANT EXECUTE ON PROCEDURE dbname.add_Provider TO 'pharmacy_db_admin';


GRANT SELECT ON dbname.medication_category TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT ON dbname.medication_types TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT ON dbname.order_status TO 'pharmacy_db_admin' WITH GRANT OPTION;

GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.doctor TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient_recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medication_diagnosis_description TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.patient TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.prescription TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.production_order TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ready_medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.sales TO 'pharmacy_db_admin' WITH GRANT OPTION;

GRANT SELECT ON dbname.search_prescription TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT ON dbname.search_ready_medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;
GRANT SELECT ON dbname.search_recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;

GRANT SYSTEM_VARIABLES_ADMIN ON *.* TO 'pharmacy_db_admin';
GRANT SESSION_VARIABLES_ADMIN ON *.* TO 'pharmacy_db_admin';
GRANT  BINLOG_ADMIN ON *.* TO 'pharmacy_db_admin';
GRANT CONNECTION_ADMIN ON *.* TO 'pharmacy_db_admin';
GRANT ROLE_ADMIN ON *.* TO 'pharmacy_db_admin';

FLUSH PRIVILEGES;
set @sql = concat("DROP USER IF EXISTS '",`login`,"'@'",`host_name`,"'");
   PREPARE stmt1 FROM @sql;
   EXECUTE stmt1;
   DEALLOCATE PREPARE stmt1;

set @sql = concat("CREATE USER IF NOT EXISTS '",`login`,"'@'",`host_name`,"' IDENTIFIED BY '",`pass`,"' DEFAULT ROLE 'pharmacy_db_admin'");
   PREPARE stmt2 FROM @sql;
   EXECUTE stmt2;
   DEALLOCATE PREPARE stmt2;

FLUSH PRIVILEGES;
END;//

DELIMITER ;

DROP PROCEDURE IF EXISTS add_Cashier;
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS  add_Cashier (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))
BEGIN
DECLARE user_name VARCHAR(255);
DECLARE word VARCHAR(255);
CREATE ROLE IF NOT EXISTS 'cashier';

GRANT EXECUTE ON PROCEDURE dbname.learn_stock_quantity TO 'cashier';

GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.doctor TO 'cashier';
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.patient TO 'cashier';


GRANT SELECT ON dbname.medicine TO 'cashier';
GRANT SELECT ON dbname.ready_medicine TO 'cashier';
GRANT SELECT ON dbname.order_status TO 'cashier';
GRANT SELECT ON dbname.search_prescription TO 'cashier';
GRANT SELECT ON dbname.search_ready_medicine TO 'cashier';
GRANT SELECT ON dbname.search_recipe TO 'cashier';
GRANT SELECT ON dbname.ingredient TO 'cashier';
GRANT SELECT ON dbname.ingredient_recipe TO 'cashier';

GRANT SELECT, INSERT ON dbname.prescription TO 'cashier';
GRANT SELECT, INSERT ON dbname.production_order TO 'cashier';
GRANT SELECT, INSERT ON dbname.sales TO 'cashier';



GRANT UPDATE(diagnosis, direction_for_use) ON dbname.prescription TO 'cashier';

GRANT UPDATE(status_id, end_date) ON dbname.production_order TO 'cashier';

GRANT UPDATE(sales_date) ON dbname.sales TO 'cashier';

FLUSH PRIVILEGES;

set @sql = concat("DROP USER IF EXISTS '",`login`,"'@'",`host_name`,"'");
   PREPARE stmt1 FROM @sql;
   EXECUTE stmt1;
   DEALLOCATE PREPARE stmt1;

set @sql = concat("CREATE USER IF NOT EXISTS '",`login`,"'@'",`host_name`,"' IDENTIFIED BY '",`pass`,"'  DEFAULT ROLE 'cashier'");
   PREPARE stmt2 FROM @sql;
   EXECUTE stmt2;
   DEALLOCATE PREPARE stmt2;

FLUSH PRIVILEGES;
END;//

DELIMITER ;

DROP PROCEDURE IF EXISTS add_Producer;
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS  add_Producer (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))
BEGIN
DECLARE user_name VARCHAR(255);
DECLARE word VARCHAR(255);
CREATE ROLE IF NOT EXISTS 'recipe_producer';

GRANT SELECT ON dbname.patient TO 'recipe_producer';
GRANT SELECT ON dbname.doctor TO 'recipe_producer';
GRANT SELECT ON dbname.medicine TO 'recipe_producer';
GRANT SELECT ON dbname.order_status TO 'recipe_producer';
GRANT SELECT ON dbname.search_prescription TO 'recipe_producer';
GRANT SELECT ON dbname.search_recipe TO 'recipe_producer';
GRANT SELECT ON dbname.prescription TO 'recipe_producer';
GRANT SELECT ON dbname.ingredient TO 'recipe_producer';
GRANT SELECT ON dbname.ingredient_recipe TO 'recipe_producer';
GRANT SELECT ON dbname.production_order TO 'recipe_producer';

GRANT UPDATE(status_id) ON dbname.production_order TO 'recipe_producer';

FLUSH PRIVILEGES;

set @sql = concat("DROP USER IF EXISTS '",`login`,"'@'",`host_name`,"'");
   PREPARE stmt1 FROM @sql;
   EXECUTE stmt1;
   DEALLOCATE PREPARE stmt1;

set @sql = concat("CREATE USER IF NOT EXISTS '",`login`,"'@'",`host_name`,"' IDENTIFIED BY '",`pass`,"'  DEFAULT ROLE 'recipe_producer'");
   PREPARE stmt2 FROM @sql;
   EXECUTE stmt2;
   DEALLOCATE PREPARE stmt2;

FLUSH PRIVILEGES;
END;//

DELIMITER ;

DROP PROCEDURE IF EXISTS add_Provider;
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS  add_Provider (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))
BEGIN
DECLARE user_name VARCHAR(255);
DECLARE word VARCHAR(255);
CREATE ROLE IF NOT EXISTS 'provider';

GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medicine TO 'provider';
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.recipe TO 'provider';
GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient_recipe TO 'provider';

GRANT SELECT ON dbname.medication_types TO 'provider';
GRANT SELECT ON dbname.medication_diagnosis_description TO 'provider';
GRANT SELECT ON dbname.search_ready_medicine TO 'provider';
GRANT SELECT ON dbname.search_recipe TO 'provider';

GRANT SELECT, INSERT ON dbname.ready_medicine TO 'provider';

GRANT SELECT, INSERT, DELETE ON dbname.ingredient TO 'provider';

GRANT UPDATE, DELETE ON dbname.ready_medicine TO 'provider';

GRANT UPDATE ON dbname.ingredient TO 'provider';

FLUSH PRIVILEGES;

set @sql = concat("DROP USER IF EXISTS '",`login`,"'@'",`host_name`,"'");
   PREPARE stmt1 FROM @sql;
   EXECUTE stmt1;
   DEALLOCATE PREPARE stmt1;

set @sql = concat("CREATE USER IF NOT EXISTS '",`login`,"'@'",`host_name`,"' IDENTIFIED BY '",`pass`,"'  DEFAULT ROLE 'provider'");
   PREPARE stmt2 FROM @sql;
   EXECUTE stmt2;
   DEALLOCATE PREPARE stmt2;

FLUSH PRIVILEGES;
END;//

DELIMITER ;

CALL add_Admin('db_admin','1','localhost');
CALL add_Cashier('db_cashier','3','localhost');
CALL add_Producer('db_producer','5','localhost');
CALL add_Provider('db_provider','7','localhost');


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
INNER JOIN medicine m ON(r.id = m.id)
INNER JOIN medication_types mt ON(m.medicament_type_id = mt.id)
LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id);

CREATE VIEW search_recipe AS
SELECT r.id, m.name_medicament, mt.name_med_type, md.name_med_diagnosis_description, r.preparation_method, r.time_hours, SUM(amount_ingredient * i.price) AS total_price FROM recipe r
INNER JOIN medicine m ON(r.id = m.id)
LEFT JOIN medication_types mt ON(m.medicament_type_id = mt.id)
LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id)
INNER JOIN ingredient_recipe ir ON (r.id = ir.id_recipe)
LEFT JOIN ingredient i ON(i.id = ir.id_ingredient)
GROUP BY r.id, m.name_medicament, mt.name_med_type, md.name_med_diagnosis_description, r.preparation_method, r.time_hours;


--------------------------------------
ВСТАВКА ТЕСТОВЫХ ДАННЫХ
--------------------------------------
INSERT INTO medication_category(name_med_category)
VALUES
('Готовое'),
('Изготовляемое'),
('Готовое/Изготовляемое');

INSERT INTO medication_types(name_med_type, name_med_category_id)
VALUES
('Мази', 3),
('Настойки', 3),
('Таблетки', 1),
('Микстуры', 2),
('Порошки', 2),
('Растворы', 3);

INSERT INTO medication_diagnosis_description(name_med_diagnosis_description)
VALUES
('Аллергия'),
('Дерматология'),
('ЖКТ'),
('Нервная система'),
('Обезболивающие'),
('Противовирусные'),
('Простуда и грипп');

INSERT INTO order_status(name_order_status)
VALUES
('Ожидание доставки ингредиентов'),
('Ожидание оплаты'),
('В процессе приготовления'),
('Готов'),
('Завершен'),
('Пропал');


INSERT INTO medicine(name_medicament, medicament_type_id, med_diagnosis_description_id, selling_without_presc)
VALUES
('Назол', 6, 7, 'продается'),
('Фестал', 3, 3,'продается'),
('Гербион сироп подорожника', 4,7, 'не продается'),
('Нейрофлексин', 3,4,'не продается'),
('Легкофикс', 1,2,'не продается'),
('Виталиндекс', 5,5,'не продается'),
('Кардиопринт', 2,4,'не продается');

INSERT INTO ready_medicine(id, stock_quantity, critical_quantity, price)
VALUES
(1,250,100,500),
(2,250,100,500),
(4,100,25,1000),
(5,100,25,1000);

INSERT INTO ingredient(name_ingredient, stock_quantity, critical_quantity, price)
VALUES
('Подорожник', 1000,500,10),
('Ацетилсалициловая кислота',500,100,100),
('Магния стеарат',500,100,100),
('Полиэтиленгликоль',500,100,100),
('Пчелиный воск',500,100,100),
('Анальгин',500,100,100);

INSERT INTO recipe(id, preparation_method, time_hours)
VALUES
(3, 'Смешать ингредиенты', 1),
(6, 'Смешать ингредиенты', 2),
(7, 'Смешать ингредиенты', 3);

INSERT INTO ingredient_recipe(id_recipe, id_ingredient, amount_ingredient)
VALUES
(3,1,10),
(3,2,10),
(6,3,10),
(6,4,10),
(6,2,10),
(7,6,10),
(7,2,10);

INSERT INTO patient(full_name_patient, phone_number, address, date_of_birth)
VALUES
('-','-','-','1900-01-01'),
('Горбачева Вера Артёмовна','7(923)480-44-99','спуск Балканская, 45','1990-06-13'),
('Морозова Алиса Константиновна','7(923)627-00-86','пл. Гагарина, 68','1980-08-07'),
('Платонов Дмитрий Максимович','7(923)805-98-22','спуск Ленина, 78','1990-01-11'),
('Ершов Савва Максимович','7(923)185-70-09','пр. Бухарестская, 29','1993-03-15'),
('Максимов Алексей Михайлович','7(953)617-17-71','пер. Косиора, 66','1981-06-29'),
('Наумова Варвара Маратовна','7(953)768-01-50','наб. Сталина, 76','1960-03-22');

INSERT INTO doctor(full_name_doctor)
VALUES
('-'),
('Борисов Дмитрий Александрович'),
('Жуков Георгий Фёдорович'),
('Медведева Екатерина Андреевна'),
('Прохорова Александра Андреевна');

INSERT INTO prescription(patient_id, medicine_id, doctor_id, amount, diagnosis, direction_for_use)
VALUES
(1,1,1,1,'-','-'),
(1,2,1,1,'-','-'),
(3,3,2,2,'Простуда', 'внутрь'),
(4,4,3,1,'Нервное рас-во', 'внутрь, 1 раз в день'),
(5,5,3,1,'Сыпь', '2 раза в день мазать очаги воспаления');

INSERT INTO production_order(id,status_id,start_date,end_date)
VALUES
(3,2,'2023-06-13', '2023-06-15'),
(4,2,'2023-06-13','2023-06-13'),
(5,2,'2023-06-13','2023-06-13');

INSERT INTO sales(id,sales_date)
VALUES
(3,'2023-06-14'),
(4,'2023-06-13'),
(5,'2023-06-13');

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