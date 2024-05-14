package com.example.informationsystem.utils;

import com.example.informationsystem.controllers.insert.InsertController;
import com.example.informationsystem.controllers.insert.ProductionOrderInsertController;
import com.example.informationsystem.utils.Connection;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
        createDBAdmin();


        System.out.println("..adding base information..");
        System.out.println("-----database successfully created-----");
    }

    public static int getIdFrom(String item) {
        Integer id = Integer.valueOf(getSubstring(" ID=", "ID=", item));
        return id.intValue();
    }

    public static int getIdFromIngRec(String item) {
        Integer id = Integer.valueOf(getSubstring(" ID рецепта=", "ID рецепта=", item));
        return id.intValue();
    }

    public static int getIdFromIngRecI(String item) {
        Integer id = Integer.valueOf(getSubstring(" ID ингредиента=", "ID ингредиента=", item));
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

    private void createDBAdmin() throws SQLException {
        String createRoleAdmin = "  CREATE PROCEDURE add_Admin (IN login VARCHAR(200), IN pass VARCHAR(200), IN host_name VARCHAR(200))  " +
                "  BEGIN  " +
                "  DECLARE user_name VARCHAR(255);  " +
                "  DECLARE word VARCHAR(255);  " +
                "    " +
                "    " +
                "  DROP ROLE IF EXISTS 'pharmacy_db_admin';  " +
                "    " +
                "  CREATE ROLE IF NOT EXISTS 'pharmacy_db_admin';  " +
                "    " +
                "  GRANT CREATE USER ON *.* TO 'pharmacy_db_admin';  " +
                "    " +
                "  GRANT RELOAD ON *.* TO 'pharmacy_db_admin';  " +
                "  GRANT CREATE ROUTINE ON dbname.* TO 'pharmacy_db_admin';  " +
                "    " +
                "  GRANT EXECUTE ON PROCEDURE dbname.learn_stock_quantity TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "    " +
                "  GRANT SELECT ON dbname.medication_category TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT ON dbname.medication_types TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT ON dbname.order_status TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "    " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.doctor TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ingredient_recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medication_diagnosis_description TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.patient TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.prescription TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.production_order TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.ready_medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT,INSERT, UPDATE, DELETE ON dbname.sales TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "    " +
                "  GRANT SELECT ON dbname.search_prescription TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT ON dbname.search_ready_medicine TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "  GRANT SELECT ON dbname.search_recipe TO 'pharmacy_db_admin' WITH GRANT OPTION;  " +
                "    " +
                "  GRANT SYSTEM_VARIABLES_ADMIN ON *.* TO 'pharmacy_db_admin';  " +
                "  GRANT SESSION_VARIABLES_ADMIN ON *.* TO 'pharmacy_db_admin';  " +
                "  GRANT  BINLOG_ADMIN ON *.* TO 'pharmacy_db_admin';  " +
                "  GRANT CONNECTION_ADMIN ON *.* TO 'pharmacy_db_admin';  " +
                "  GRANT ROLE_ADMIN ON *.* TO 'pharmacy_db_admin';  " +
                "    " +
                "  FLUSH PRIVILEGES;  " +
                "  set @sql = concat(\"DROP USER IF EXISTS '\",`login`,\"'@'\",`host_name`,\"'\");  " +
                "     PREPARE stmt1 FROM @sql;  " +
                "     EXECUTE stmt1;  " +
                "     DEALLOCATE PREPARE stmt1;  " +
                "       " +
                "  set @sql = concat(\"CREATE USER IF NOT EXISTS '\",`login`,\"'@'\",`host_name`,\"' IDENTIFIED BY '\",`pass`,\"' DEFAULT ROLE 'pharmacy_db_admin'\");  " +
                "     PREPARE stmt2 FROM @sql;  " +
                "     EXECUTE stmt2;  " +
                "     DEALLOCATE PREPARE stmt2;  " +
                "    " +
                "  FLUSH PRIVILEGES;  " +
                "  END;  ";

        connection.executeQuery(createRoleAdmin);
    }

    private void createTriggers() throws SQLException {
        //Place Triggers
        String trigger1 = "CREATE TRIGGER check_on_ready_medicine BEFORE INSERT ON production_order " +
        "FOR EACH ROW " +
         "      BEGIN " +
        "DECLARE msg VARCHAR(128); " +

        "if(NEW.id IN (SELECT id FROM prescription " +
        "        WHERE medicine_id IN (SELECT id FROM search_ready_medicine WHERE selling_without_presc = 'продается'))) THEN " +
        "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – некорректные данные!'; " +
        "END if; " +
        "END;";



        connection.executeQuery("DROP TRIGGER if exists check_on_ready_medicine; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger1);
        //connection.executeQuery("        delimiter ;");

        String trigger2 = "CREATE TRIGGER insert_sale_of_ready_med " +
        "AFTER INSERT " +
        "ON prescription FOR EACH ROW " +
        "        BEGIN " +
        "if(NEW.medicine_id IN (SELECT id FROM search_ready_medicine WHERE selling_without_presc = 'продается')) THEN " +
        "INSERT INTO sales(id, sales_date) " +
        "VALUES(NEW.ID, CURRENT_DATE()); " +
        "END IF; " +
        "END;";

        connection.executeQuery("DROP TRIGGER if exists insert_sale_of_ready_med; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger2);
        //connection.executeQuery("        delimiter ;");

        String trigger3 = "CREATE TRIGGER insert_info_of_ready_med_without_presc " +
        "BEFORE INSERT " +
        "ON prescription FOR EACH ROW " +
        "        BEGIN " +
        "IF(NEW.medicine_id IN (SELECT id FROM medicine WHERE selling_without_presc = 'не продается') AND (NEW.patient_id = 1 OR NEW.doctor_id = 1)) THEN " +
        "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – при продаже лек-ва, для которого нужен рецепт, пациент и доктор должны быть конкретизированы!'; " +
        "END IF; " +
        "END;";

        connection.executeQuery("DROP TRIGGER if exists insert_info_of_ready_med_without_presc; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger3);
        //connection.executeQuery("        delimiter ;");

        String trigger4 = "  CREATE TRIGGER check_on_stock_quantity_update_ready_med BEFORE UPDATE ON ready_medicine  " +
                "  FOR EACH ROW  "+
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "  	DECLARE cur_role VARCHAR(128);  "+
                "  SELECT USER() INTO cur_role;  " +
                " 	if(OLD.stock_quantity > NEW.stock_quantity AND (cur_role LIKE '%provider%')) THEN  "+
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Нельзя уменьшать кол-во лекарства на складе без продажи оного!';  "+
                "  	END if;  "+
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_stock_quantity_update_ready_med; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger4);
        //connection.executeQuery("        delimiter ;");

        String trigger5 = "  CREATE TRIGGER check_on_stock_quantity_update_ingredient BEFORE UPDATE ON ingredient  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "  DECLARE cur_role VARCHAR(128);  " +
                "  DECLARE test_p VARCHAR(128);  " +
                "    " +
                "  SELECT USER() INTO cur_role;  " +
                "    " +
                "  if(OLD.stock_quantity > NEW.stock_quantity AND (cur_role LIKE '%provider%')) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Нельзя уменьшать кол-во инг-та на складе без исполльзования в продаже оного!';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_stock_quantity_update_ingredient; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger5);
        //connection.executeQuery("        delimiter ;");

        String trigger6 = "  CREATE TRIGGER insert_medicine_selling_presc BEFORE INSERT ON medicine  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  if(NEW.selling_without_presc = 'продается' AND NEW.medicament_type_id IN (SELECT id FROM medication_types WHERE name_med_category_id = 2)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Лек-ва категории Изготовляемое требуют наличие рецепта при продаже';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists insert_medicine_selling_presc; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger6);
        //connection.executeQuery("        delimiter ;");

        String trigger7 = "  CREATE TRIGGER delete_medicine_in_presc BEFORE DELETE ON medicine  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  if(OLD.id IN (SELECT medicine_id FROM prescription)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Лек-во уже было использовано при оформлении заказа. Удаление запрещено';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists delete_medicine_in_presc; ");
        //connection.executeQuery("delimiter // ");
        connection.executeQuery(trigger7);
        //connection.executeQuery("        delimiter ;");

        String trigger8 = "  CREATE TRIGGER delete_patient_in_presc BEFORE DELETE ON patient  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  if(OLD.id IN (SELECT patient_id FROM prescription)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Id Пациента уже было использовано при оформлении заказа. Удаление запрещено';  " +
                "  END if;  " +
                "  if(OLD.full_name_patient = '-') THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Неопределенный ID нужен для продажи лек-в, не требующих рецепта. Удаление запрещено';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists delete_patient_in_presc; ");
        connection.executeQuery(trigger8);

        String trigger9 = "  CREATE TRIGGER delete_doctor_in_presc BEFORE DELETE ON doctor  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  if(OLD.id IN (SELECT doctor_id FROM prescription)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Id Доктора уже было использовано при оформлении заказа. Удаление запрещено';  " +
                "  END if;  " +
                "  if(OLD.full_name_doctor = '-') THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Неопределенный ID нужен для продажи лек-в, не требующих рецепта. Удаление запрещено';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists delete_doctor_in_presc; ");
        connection.executeQuery(trigger9);

        String trigger10 = "  CREATE TRIGGER delete_ingredient_in_recipe BEFORE DELETE ON ingredient  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  if(OLD.id IN (SELECT id_ingredient FROM ingredient_recipe)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ингредиент используется в каком-то рецепте. Удаление запрещено';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists delete_ingredient_in_recipe; ");
        connection.executeQuery(trigger10);

        String trigger11 = "  CREATE TRIGGER update_end_date_in_prescription BEFORE UPDATE ON production_order  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE sales_date_pro DATE;  " +
                "    " +
                "  SELECT sales_date INTO sales_date_pro FROM sales WHERE id = OLD.id;  " +
                "    " +
                "  if(NEW.end_date < OLD.start_date OR NEW.end_date < sales_date_pro) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата закрытия заказа не может быть ранее чем открытия или оплаты';  " +
                "  END if;  " +
                "  END;  ";
        connection.executeQuery("DROP TRIGGER if exists update_end_date_in_prescription; ");
        connection.executeQuery(trigger11);

        String trigger12 = "  CREATE TRIGGER insert_sales_date_in_prescription BEFORE INSERT ON sales  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE start_date_pro DATE;  " +
                "  DECLARE end_date_pro DATE;  " +
                "    " +
                "  IF(NEW.id IN(SELECT id FROM production_order)) THEN  " +
                "  SELECT start_date INTO start_date_pro FROM production_order WHERE id = NEW.id;  " +
                "    " +
                "  if(NEW.sales_date < start_date_pro) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата оплаты заказа не может быть ранее чем открытия';  " +
                "  END if;  " +
                "    " +
                "  SELECT end_date INTO start_date_pro FROM production_order WHERE id = NEW.id;  " +
                "    " +
                "  if(NEW.sales_date > start_date_pro) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Дата оплаты заказа не может быть позднее чем закрытия';  " +
                "  END if;  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists insert_sales_date_in_prescription; ");
        connection.executeQuery(trigger12);

        String trigger13 = "  CREATE TRIGGER check_on_order_status_insert BEFORE INSERT ON production_order  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "    " +
                "  if(NEW.status_id <> 1 AND NEW.status_id <> 2) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Вставка отменена – некорректные данные!';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_order_status_insert; ");
        connection.executeQuery(trigger13);

        String trigger14 = "  CREATE TRIGGER check_on_order_status_ingredient_ready_is_enough BEFORE INSERT ON production_order  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "    " +
                "  DECLARE n INT DEFAULT 0;  " +
                "  DECLARE i INT DEFAULT 0;  " +
                "  DECLARE id_ing INT DEFAULT 0;  " +
                "  DECLARE stock_quantity_ing INT DEFAULT 0;  " +
                "  DECLARE check_on_enough INT;  " +
                "  DECLARE medicine_id_pro INT;  " +
                "  DECLARE amount_pro INT;  " +
                "  DECLARE stock_quantity_pro INT DEFAULT 0;  " +
                "    " +
                "  SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;  " +
                "  SELECT amount INTO amount_pro FROM prescription WHERE id = NEW.id;  " +
                "    " +
                "    " +
                "  SET check_on_enough = 1;  " +
                "           " +
                "     if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN  " +
                "  SELECT COUNT(*) FROM ingredient INTO n;  " +
                "  SET i=0;  " +
                "  WHILE i<n DO  " +
                "  SELECT id INTO id_ing FROM ingredient LIMIT i,1;  " +
                "  SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;  " +
                "     " +
                "  IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  SET i = i + 1;  " +
                "  END WHILE;  " +
                "    " +
                "  IF (check_on_enough = 0) THEN  " +
                "  SET NEW.status_id = 1;  " +
                "  END IF;  " +
                "  END if;  " +
                "    " +
                "  if NEW.status_id = 1 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN  " +
                "  SELECT COUNT(*) FROM ingredient INTO n;  " +
                "  SET i=0;  " +
                "  WHILE i<n DO  " +
                "  SELECT id INTO id_ing FROM ingredient LIMIT i,1;  " +
                "  SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;  " +
                "      " +
                "  IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  SET i = i + 1;  " +
                "  END WHILE;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  SET NEW.status_id = 2;  " +
                "  END IF;  " +
                "  END if;  " +
                "    " +
                "  if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN  " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;  " +
                "    " +
                "  IF(stock_quantity_pro - amount_pro < 0) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  IF (check_on_enough = 0) THEN  " +
                "  SET NEW.status_id = 1;  " +
                "  END IF;  " +
                "  END if;  " +
                "    " +
                "  if NEW.status_id = 1 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN  " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;  " +
                "    " +
                "  IF(stock_quantity_pro - amount_pro < 0) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  SET NEW.status_id = 2;  " +
                "  END IF;  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_order_status_ingredient_ready_is_enough; ");
        connection.executeQuery(trigger14);

        String trigger15 = "  CREATE TRIGGER check_on_order_status_ingredient_ready_sales_is_ready BEFORE UPDATE ON production_order  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "    " +
                "  DECLARE n INT DEFAULT 0;  " +
                "  DECLARE i INT DEFAULT 0;  " +
                "  DECLARE id_ing INT DEFAULT 0;  " +
                "  DECLARE stock_quantity_ing INT DEFAULT 0;  " +
                "  DECLARE check_on_enough INT;  " +
                "  DECLARE medicine_id_pro INT;  " +
                "  DECLARE amount_pro INT;  " +
                "  DECLARE stock_quantity_pro INT DEFAULT 0;  " +
                "    " +
                "  SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;  " +
                "  SELECT amount INTO amount_pro FROM prescription WHERE id = NEW.id;  " +
                "    " +
                "    " +
                "    " +
                "  SET check_on_enough = 1;  " +
                "           " +
                "     if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id_recipe FROM ingredient_recipe) THEN  " +
                "  SELECT COUNT(*) FROM ingredient INTO n;  " +
                "  SET i=0;  " +
                "  WHILE i<n DO  " +
                "  SELECT id INTO id_ing FROM ingredient LIMIT i,1;  " +
                "  SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;  " +
                "      " +
                "  IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity_ing - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id_ing) < 0))) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  SET i = i + 1;  " +
                "  END WHILE;  " +
                "   " +
                "  IF (check_on_enough = 0) THEN  " +
                "  SET NEW.status_id = 1;  " +
                "  END IF;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  UPDATE ingredient_stock  " +
                "        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =medicine_id_pro AND id_ingredient = id) >= 0)), stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id), stock_quantity);  " +
                "    " +
                "  UPDATE ingredient  " +
                "        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro) AND (stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =medicine_id_pro AND id_ingredient = id) >= 0)), stock_quantity - amount_pro * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = medicine_id_pro AND id_ingredient = id), stock_quantity);  " +
                "  END if;  " +
                "  END if;  " +
                "    " +
                "  if NEW.status_id = 2 AND medicine_id_pro IN (SELECT id FROM ready_medicine) THEN  " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = medicine_id_pro;  " +
                "    " +
                "  IF(stock_quantity_pro - amount_pro < 0) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "   " +
                "  IF (check_on_enough = 0) THEN  " +
                "  SET NEW.status_id = 1;  " +
                "  END IF;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  UPDATE ready_stock  " +
                "  SET stock_quantity = stock_quantity_pro - amount_pro  " +
                "  WHERE id = medicine_id_pro;  " +
                "    " +
                "  UPDATE ready_medicine  " +
                "  SET stock_quantity = stock_quantity_pro - amount_pro  " +
                "  WHERE id = medicine_id_pro;  " +
                "  END if;  " +
                "  END if;  " +
                "    " +
                "  if NEW.status_id = 3 AND NEW.id NOT IN (SELECT id FROM sales) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Заказ ещё не оплачен. Для этого надо определить дату.';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_order_status_ingredient_ready_sales_is_ready; ");
        connection.executeQuery(trigger15);

        String trigger16 = "  CREATE TRIGGER check_on_order_status_update BEFORE UPDATE ON production_order  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE msg VARCHAR(128);  " +
                "  DECLARE medicine_id_pro INT DEFAULT 0;  " +
                "    " +
                "  SELECT medicine_id INTO medicine_id_pro FROM prescription WHERE id = NEW.id;  " +
                "    " +
                "  if(OLD.status_id = 1 AND NEW.status_id <> 2) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Ожидание оплаты';  " +
                "  END if;  " +
                "  if(OLD.status_id = 2 AND NEW.status_id <> 3 AND medicine_id_pro IN (SELECT id FROM recipe)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет В процессе приготовления';  " +
                "  END if;  " +
                "  if(OLD.status_id = 3 AND NEW.status_id <> 4 AND medicine_id_pro IN (SELECT id FROM recipe)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Готов';  " +
                "  END if;  " +
                "  if(OLD.status_id = 4 AND (NEW.status_id <> 5 AND NEW.status_id <> 6)) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. После идет Завершен или Пропал';  " +
                "  END if;  " +
                "    " +
                "    " +
                "  if(OLD.status_id = 2 AND NEW.status_id <> 4 AND medicine_id_pro NOT IN (SELECT id FROM recipe)) THEN  " +
                "  SET NEW.status_id = 4;  " +
                "  END if;  " +
                "  if(OLD.status_id = 3 AND NEW.status_id <> 4 AND medicine_id_pro NOT IN (SELECT id FROM recipe)) THEN  " +
                "  SET NEW.status_id = 4;  " +
                "  END if;  " +
                "    " +
                "  if(OLD.status_id = 5 AND NEW.status_id <> 5) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Этот статус конечный';  " +
                "  END if;  " +
                "  if(OLD.status_id = 6 AND NEW.status_id <> 6) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Некорректный порядок смены статуса. Этот статус конечный';  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists check_on_order_status_update; ");
        connection.executeQuery(trigger16);

        String trigger17 = "  CREATE TRIGGER insert_ingredient_stock AFTER INSERT ON ingredient  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  INSERT INTO ingredient_stock (id, name_ingredient, stock_quantity) VALUES (NEW.id, NEW.name_ingredient, NEW.stock_quantity);  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists insert_ingredient_stock; ");
        connection.executeQuery(trigger17);

        String trigger18 = "  CREATE TRIGGER update_ingredient_stock_after_presc BEFORE INSERT ON prescription  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE n INT DEFAULT 0;  " +
                "  DECLARE i INT DEFAULT 0;  " +
                "  DECLARE id_ing INT DEFAULT 0;  " +
                "  DECLARE stock_quantity_ing INT DEFAULT 0;  " +
                "  DECLARE check_on_enough INT;  " +
                "    " +
                "  SET check_on_enough = 1;  " +
                "           " +
                "     if NEW.medicine_id IN (SELECT id_recipe FROM ingredient_recipe) THEN  " +
                "  SELECT COUNT(*) FROM ingredient INTO n;  " +
                "  SET i=0;  " +
                "  WHILE i<n DO  " +
                "  SELECT id INTO id_ing FROM ingredient LIMIT i,1;  " +
                "  SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;  " +
                "      " +
                "  IF((id_ing IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity_ing - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id_ing) < 0))) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "    " +
                "  SET i = i + 1;  " +
                "  END WHILE;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  UPDATE ingredient_stock  " +
                "        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =NEW.medicine_id AND id_ingredient = id) >= 0)), stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id), stock_quantity);  " +
                "    " +
                "  UPDATE ingredient  " +
                "        SET stock_quantity = IF((id IN (SELECT id_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id) AND (stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe =NEW.medicine_id AND id_ingredient = id) >= 0)), stock_quantity - NEW.amount * (SELECT amount_ingredient FROM ingredient_recipe WHERE id_recipe = NEW.medicine_id AND id_ingredient = id), stock_quantity);  " +
                "  END IF;  " +
                "  END if;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists update_ingredient_stock_after_presc; ");
        connection.executeQuery(trigger18);

        String trigger19 = "  CREATE TRIGGER update_ingredient_stock BEFORE UPDATE ON ingredient  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE stock_quantity_pro INT DEFAULT 0;  " +
                "    " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ingredient_stock WHERE id = NEW.id;  " +
                "    " +
                "  IF(NEW.stock_quantity >= stock_quantity_pro) THEN  " +
                "  UPDATE ingredient_stock  " +
                "        SET stock_quantity = NEW.stock_quantity  " +
                "        WHERE id = NEW.id;  " +
                "  END IF;  " +
                "    " +
                "  IF(NEW.stock_quantity < stock_quantity_pro) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Запрещено уменьшать кол-во ингредиента со склада без оформления заказа, где он используется';  " +
                "  END IF;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists update_ingredient_stock; ");
        connection.executeQuery(trigger19);

        String trigger20 = "  CREATE TRIGGER insert_ready_stock AFTER INSERT ON ready_medicine  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  INSERT INTO ready_stock (id, stock_quantity) VALUES (NEW.id, NEW.stock_quantity);  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists insert_ready_stock; ");
        connection.executeQuery(trigger20);

        String trigger21 = "  CREATE TRIGGER update_ready_stock_after_presc BEFORE INSERT ON prescription  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE n INT DEFAULT 0;  " +
                "  DECLARE i INT DEFAULT 0;  " +
                "  DECLARE id_ing INT DEFAULT 0;  " +
                "  DECLARE stock_quantity_pro INT DEFAULT 0;  " +
                "  DECLARE check_on_enough INT;  " +
                "    " +
                "  SET check_on_enough = 1;  " +
                "           " +
                "     if NEW.medicine_id IN (SELECT id FROM ready_medicine) THEN  " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ready_medicine WHERE id = NEW.medicine_id;  " +
                "    " +
                "  IF(stock_quantity_pro - NEW.amount < 0) THEN  " +
                "  SET check_on_enough = 0;  " +
                "  END IF;  " +
                "  END if;  " +
                "    " +
                "  IF (check_on_enough = 1) THEN  " +
                "  UPDATE ready_stock  " +
                "  SET stock_quantity = stock_quantity_pro - NEW.amount  " +
                "  WHERE id = NEW.medicine_id;  " +
                "    " +
                "  UPDATE ready_medicine  " +
                "  SET stock_quantity = stock_quantity_pro - NEW.amount  " +
                "  WHERE id = NEW.medicine_id;  " +
                "  END IF;  " +
                "  END;  ";

        connection.executeQuery("DROP TRIGGER if exists update_ready_stock_after_presc; ");
        connection.executeQuery(trigger21);

        String trigger22 = "    " +
                "  CREATE TRIGGER update_ready_stock BEFORE UPDATE ON ready_medicine  " +
                "  FOR EACH ROW  " +
                "  BEGIN  " +
                "  DECLARE stock_quantity_pro INT DEFAULT 0;  " +
                "    " +
                "  SELECT stock_quantity INTO stock_quantity_pro FROM ready_stock WHERE id = NEW.id;  " +
                "    " +
                "  IF(NEW.stock_quantity >= stock_quantity_pro) THEN  " +
                "  UPDATE ready_stock  " +
                "        SET stock_quantity = NEW.stock_quantity  " +
                "        WHERE id = NEW.id;  " +
                "  END IF;  " +
                "    " +
                "  IF(NEW.stock_quantity < stock_quantity_pro) THEN  " +
                "  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Запрещено уменьшать кол-во лек-ва со склада без оформления заказа, где он используется';  " +
                "  END IF;  " +
                "  END;  ";
        connection.executeQuery("DROP TRIGGER if exists update_ready_stock; ");
        connection.executeQuery(trigger22);
    }

    public void createProcedure() throws SQLException {
        //Place Procedures
        String procedure1 = "  CREATE PROCEDURE learn_stock_quantity (IN pre_med_id INTEGER, IN amount INTEGER)  " +
        "         BEGIN  " +
                "           " +
                "         DECLARE n INT DEFAULT 0;  " +
                "   DECLARE i INT DEFAULT 0;  " +
                "   DECLARE id_ing INT DEFAULT 0;  " +
                "   DECLARE amount_ing INT DEFAULT 0;  " +
                "   DECLARE stock_quantity_ing INT DEFAULT 0;  " +
                "    " +
                "           " +
                "         if (pre_med_id IN (SELECT id_recipe FROM ingredient_recipe)) THEN  " +
                "           " +
                "         DROP TEMPORARY TABLE if exists conclusion;  " +
                "           " +
                "         CREATE TEMPORARY TABLE if NOT EXISTS conclusion(  " +
                "  id_recipe                INT NOT NULL,  " +
                "  id_ingredient                INT NOT NULL,  " +
                "  total_amount INT NOT NULL,  " +
                "  verdict VARCHAR(128) NOT NULL,  " +
                "    " +
                "  PRIMARY KEY (id_recipe , id_ingredient)  " +
                "  );  " +
                "    " +
                "  SELECT COUNT(*) FROM ingredient_recipe WHERE id_recipe = pre_med_id INTO n;  " +
                "  SET i=0;  " +
                "  WHILE i<n DO  " +
                "  SELECT id_ingredient, amount_ingredient*amount INTO id_ing, amount_ing FROM ingredient_recipe WHERE id_recipe = pre_med_id LIMIT i,1;  " +
                "  SELECT stock_quantity INTO stock_quantity_ing FROM ingredient WHERE id = id_ing;  " +
                "    " +
                "  if(stock_quantity_ing >= amount_ing) THEN  " +
                "  INSERT INTO conclusion(id_recipe, id_ingredient, total_amount, verdict)  " +
                "  VALUES(pre_med_id,id_ing,amount_ing,'Хватает');  " +
                "  END if;  " +
                "    " +
                "  if(stock_quantity_ing < amount_ing) THEN  " +
                "  INSERT INTO conclusion(id_recipe, id_ingredient, total_amount, verdict)  " +
                "  VALUES(pre_med_id,id_ing,amount_ing,'Не хватает');  " +
                "  END if;  " +
                "    " +
                "  SET i = i + 1;  " +
                "  END WHILE;  " +
                "    " +
                "  SELECT name_medicament AS recipe_medicament, name_ingredient, total_amount, verdict FROM conclusion  " +
                "  INNER JOIN medicine ON (medicine.id = id_recipe)  " +
                "  INNER JOIN ingredient ON (ingredient.id = id_ingredient)  " +
                "  WHERE id_recipe = pre_med_id;  " +
                "    " +
                "  DROP TEMPORARY TABLE conclusion;  " +
                "     END IF;  " +
                "       " +
                "     if (pre_med_id IN (SELECT id FROM ready_medicine)) THEN  " +
                "    SELECT stock_quantity INTO stock_quantity_ing FROM ready_medicine WHERE id = pre_med_id;  " +
                "      " +
                "  if(stock_quantity_ing >= amount) THEN  " +
                "  SELECT name_medicament, stock_quantity, 'Хватает' FROM ready_medicine  " +
                "  INNER JOIN medicine ON (medicine.id = ready_medicine.id)  " +
                "  WHERE ready_medicine.id = pre_med_id;  " +
                "  END if;  " +
                "    " +
                "  if(stock_quantity_ing < amount) THEN  " +
                "  SELECT name_medicament, stock_quantity, 'Не хватает' FROM ready_medicine  " +
                "  INNER JOIN medicine ON (medicine.id = ready_medicine.id)  " +
                "  WHERE ready_medicine.id = pre_med_id;  " +
                "  END if;  " +
                "    " +
                " END IF;   " +
                "         END;  ";

        connection.executeQuery("DROP PROCEDURE if EXISTS learn_stock_quantity; ");
        connection.executeQuery(procedure1);
    }

    public void createViews() throws SQLException {
        String view_search_prescription ="  CREATE VIEW search_prescription AS   " +
                "  SELECT pre.id, p.full_name_patient, m.name_medicament, md.name_med_diagnosis_description, d.full_name_doctor, amount, diagnosis, direction_for_use,  " +
                "  pro.start_date, pro.end_date, s.sales_date, os.name_order_status FROM prescription pre  " +
                "  INNER JOIN patient p ON(pre.patient_id = p.id)  " +
                "  INNER JOIN medicine m ON(pre.medicine_id = m.id)  " +
                "  INNER JOIN doctor d ON(pre.doctor_id = d.id)  " +
                "  LEFT JOIN production_order pro ON(pre.id = pro.id)  " +
                "  LEFT JOIN sales s ON(pre.id = s.id)  " +
                "  LEFT JOIN order_status os ON(pro.status_id = os.id)  " +
                "  LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id);  ";
        connection.executeQuery(view_search_prescription);

        String view_search_ready_medicine = "  CREATE VIEW search_ready_medicine AS   " +
                "  SELECT r.id, m.name_medicament, m.selling_without_presc, mt.name_med_type, md.name_med_diagnosis_description, r.stock_quantity, r.critical_quantity, r.price FROM ready_medicine r  " +
                "  INNER JOIN medicine m ON(r.id = m.id)  " +
                "  INNER JOIN medication_types mt ON(m.medicament_type_id = mt.id)  " +
                "  LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id)  ";

        connection.executeQuery("DROP VIEW if exists search_ready_medicine; ");
        connection.executeQuery(view_search_ready_medicine);

        String view_search_recipe ="  CREATE VIEW search_recipe AS  " +
                "  SELECT r.id, m.name_medicament, mt.name_med_type, md.name_med_diagnosis_description, r.preparation_method, r.time_hours, SUM(amount_ingredient * i.price) AS total_price FROM recipe r  " +
                "  INNER JOIN medicine m ON(r.id = m.id)  " +
                "  LEFT JOIN medication_types mt ON(m.medicament_type_id = mt.id)  " +
                "  LEFT JOIN medication_diagnosis_description md ON(m.med_diagnosis_description_id = md.id)  " +
                "  INNER JOIN ingredient_recipe ir ON (r.id = ir.id_recipe)  " +
                "  LEFT JOIN ingredient i ON(i.id = ir.id_ingredient)  " +
                "  GROUP BY r.id, m.name_medicament, mt.name_med_type, md.name_med_diagnosis_description, r.preparation_method, r.time_hours  ";

        connection.executeQuery("DROP VIEW if exists search_recipe; ");
        connection.executeQuery(view_search_recipe);
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
                "stock_quantity	INT NOT NULL," +
                "critical_quantity INT NOT NULL," +
                "price INT NOT NULL," +

                "FOREIGN KEY (id) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE" +
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
                "PRIMARY KEY (id_recipe , id_ingredient),"+
                "FOREIGN KEY (id_recipe) REFERENCES recipe(id) ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY (id_ingredient) REFERENCES ingredient(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateRecipeIngredientTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create recipe_ingredient table");
            throwables.printStackTrace();
        }

        String sqlCreateIngredientStockTable = "  CREATE TABLE  if NOT exists ingredient_stock(  " +
                "   id                INT PRIMARY KEY NOT NULL,  " +
                "      name_ingredient VARCHAR(255) NOT NULL,  " +
                "      stock_quantity INT NOT NULL,  " +
                "    " +
                "  FOREIGN KEY (id) REFERENCES ingredient(id) ON UPDATE CASCADE ON DELETE CASCADE  " +
                "  );  ";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateIngredientStockTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create ingredient_stock table");
            throwables.printStackTrace();
        }

        String sqlCreateReadyStockTable = "  CREATE TABLE  if NOT exists ready_stock(  " +
                "   id                INT PRIMARY KEY NOT NULL,  " +
                "      stock_quantity INT NOT NULL,  " +
                "    " +
                "  FOREIGN KEY (id) REFERENCES ready_medicine(id) ON UPDATE CASCADE ON DELETE CASCADE  " +
                "  );  ";

        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateReadyStockTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create ready_stock table");
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

        insertReadyMedicine(1,  100, 25, 100);
        insertReadyMedicine(2,  100, 25,100);

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

        insertProductionOrder(2,1,Date.valueOf("2024-01-12"), Date.valueOf("2024-01-15"));
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

    public Integer insertMedicine (String nameMedicament, Integer nameMedicamentTypeId, Integer nameMedDiagDescId, String sellingWithoutPresc) {
        PreparedStatement preparedStatement = null;
        Integer lastInsertId = 0;
        String sqlInsertTable = "INSERT INTO medicine(name_medicament, medicament_type_id, med_diagnosis_description_id, selling_without_presc) " +
                "VALUES ('" + nameMedicament + "', '" + nameMedicamentTypeId + "','" +nameMedDiagDescId + "','" + sellingWithoutPresc + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();

            ResultSet generatedKeyResult = preparedStatement.getGeneratedKeys();

            if (generatedKeyResult.next()) {
                lastInsertId = generatedKeyResult.getInt(1);
            }
        } catch (SQLException throwables) {
            if (Objects.equals(throwables.getMessage(), "Лек-ва категории Изготовляемое требуют наличие рецепта при продаже")) {
                showAlert("Вставка отменена", "Лек-ва категории Изготовляемое требуют наличие рецепта при продаже");
            }
            System.out.println("can't insert into medicine table");
            throwables.printStackTrace();
        }

        return lastInsertId;
    }

    public void insertReadyMedicine (Integer id, Integer stockQuantity, Integer criticalQuantity, Integer price) {
        PreparedStatement preparedStatement = null;
        String sqlInsertTable = "INSERT INTO ready_medicine(id, stock_quantity, critical_quantity, price) " +
                "VALUES ('" + id + "', '" + stockQuantity + "', '" + criticalQuantity + "','" + price + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable);
            preparedStatement.executeUpdate();
            showAlert("Завершено", "Лек-во добавлено в список готовых лекарств");
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

    public Integer insertRecipe (Integer id, String preparationMethod, Integer timeHours) {
        PreparedStatement preparedStatement = null;
        Integer lastInsertId = 0;

        String sqlInsertTable = "INSERT INTO recipe(id, preparation_method, time_hours)" +
                "VALUES ('" + id + "', '" + preparationMethod + "','"+ timeHours + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertTable, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();

            ResultSet generatedKeyResult = preparedStatement.getGeneratedKeys();

            if (generatedKeyResult.next()) {
                lastInsertId = generatedKeyResult.getInt(1);
            }
        } catch (SQLException throwables) {
            System.out.println("can't insert into recipe table");
            throwables.printStackTrace();
        }

        return lastInsertId;
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

    public void updateMedicationCategory(int id, String name) throws SQLException {
        String sql = "UPDATE medication_category SET " +
                " name_med_category = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateMedicationTypes(int id, String name, Integer medicationCategoryId) throws SQLException {
        String sql = "UPDATE medication_types SET " +
                " name_med_type = '" + name + "'," +
                " name_med_category_id = " + medicationCategoryId + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateMedicationDiagnosisDescription(int id, String name) throws SQLException {
        String sql = "UPDATE medication_diagnosis_description SET " +
                " name_med_diagnosis_description = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateMedicine(int id, String name, Integer medicationTypeId, Integer medicationDescId, String sellingWithoutPresc) throws SQLException {
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

    public void updateReadyMedicine(int id, Integer stockQuantity, Integer criticalQuantity, Integer price) throws SQLException {

        String sql = "UPDATE ready_medicine SET " +
                " stock_quantity = " + stockQuantity + "," +
                " critical_quantity = " + criticalQuantity + "," +
                " price= " + price + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }

    public void updateReadyMedicineCriticalQuantity(int id,Integer criticalQuantity) throws SQLException {

        String sql = "UPDATE ready_medicine SET " +
                " critical_quantity = " + criticalQuantity +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }

    public void updateReadyMedicineStockQuantity(int id,Integer criticalQuantity) throws SQLException {

        String sql = "UPDATE ready_medicine SET " +
                " stock_quantity = " + criticalQuantity +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        try {
            connection.insert(employee_category);
        }
        catch (SQLException throwables) {
            if (throwables.getErrorCode() == 21) {
                showAlert("Вставка отменена", "Нельзя уменьшать кол-во лекарства на складе без продажи оного!");
            }
        }
        System.out.println("UPDATE employee_category_type");

    }

    public void updateReadyMedicinePrice(int id,Integer price) throws SQLException {

        String sql = "UPDATE ready_medicine SET " +
                " price = " + price +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }

    public void updateDoctor(int id, String name) throws SQLException {
        String sql = "UPDATE doctor SET " +
                " full_name_doctor = '" + name + "'" +
                " WHERE id = " + id;
        System.out.println(sql);
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updatePatient(int id, String name, String phoneNumber, String address, String dateOfBirth) throws SQLException {
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
                                   String diagnosis, String directionForUse) throws SQLException {
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

    public void updateSecondAttributesPrescription(int id, String diagnosis, String directionForUse) throws SQLException {
        String sql = "UPDATE prescription SET " +
                " diagnosis = '" + diagnosis + "'," +
                " direction_for_use = '" + directionForUse + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }

    public void updateOrderStatus(int id, String name) throws SQLException {
        String sql = "UPDATE order_status SET " +
                " name_order_status = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateProductionOrder(int id, Integer statusId, Date startDate, Date endDate) throws SQLException {
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

    public void updateProductionOrderEndDate(int id, Date endDate) throws SQLException {
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
        try {
            connection.insert(employee_category);
        }
        catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            if (Objects.equals(throwables.getMessage(), "Некорректный порядок смены статуса. После идет Ожидание оплаты")) {
                showAlert("Вставка отменена", "Некорректный порядок смены статуса.\n После идет 'Ожидание оплаты'");
            }
            if (Objects.equals(throwables.getMessage(), "Некорректный порядок смены статуса. После идет В процессе приготовления")) {
                showAlert("Вставка отменена", "Некорректный порядок смены статуса.\n После идет 'В процессе приготовления'");
            }
            if (Objects.equals(throwables.getMessage(), "Некорректный порядок смены статуса. После идет Готов")) {
                showAlert("Вставка отменена", "Некорректный порядок смены статуса.\n После идет 'Готов'");
            }
            if (Objects.equals(throwables.getMessage(), "Некорректный порядок смены статуса. После идет Завершен или Пропал")) {
                showAlert("Вставка отменена", "Некорректный порядок смены статуса.\n После идет 'Завершен' или 'Пропал'");
            }
            if (Objects.equals(throwables.getMessage(), "Некорректный порядок смены статуса. Этот статус конечный")) {
                showAlert("Вставка отменена", "Данный статус конечный. Дальнейшая смена не предполагается.");
            }
            System.out.println("can't insert into prescription table");
            throwables.printStackTrace();
        }

        System.out.println("UPDATE employee_category_type");
    }

    public void updateSales(int id, Date salesDate) throws SQLException {
        String sql = "UPDATE sales SET " +
                " sales_date = '" + salesDate + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateRecipe(int id, String preparationMethod, Integer timeHours) throws SQLException {
        String sql = "UPDATE recipe SET " +
                " preparation_method = '" + preparationMethod + "'," +
                " time_hours =" + timeHours + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateIngredient(int id, String nameIngredient, Integer stockQuantity, Integer criticalQuantity, Integer price) throws SQLException {

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

    public void updateRecipeIngredient(int recipeId, int ingredientId, Integer amount) throws SQLException {

        String sql = "UPDATE ingredient_recipe SET " +
                " amount_ingredient= " + amount + "" +
                " WHERE id_recipe = " + recipeId + " AND id_ingredient=" + ingredientId + "";
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");

    }
}
