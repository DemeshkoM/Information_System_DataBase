package com.example.informationsystem.utils;

import com.example.informationsystem.utils.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
            "employee_category.sql",
            "employee_category_type.sql"
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
        insertInfo();
        createProcedure();
        System.out.println("..adding base information..");
        System.out.println("-----database successfully created-----");
    }

    public static int getIdFrom(String item) {
        Integer id = Integer.valueOf(getSubstring(" id=", "id=", item));
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
    }

    public void createProcedure() throws SQLException {
        //Place Procedures
    }

    public void createIndex(String tableName, String columnName, String indexName) {
        //Creation of Index
    }

    public void dropTables() {
        dropTable("employee_category_type");
        dropTable("employee_category");
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

    public void createTables() {
        PreparedStatement preparedStatement = null;

        String sqlCreateEmployeeCategoryTable = "create table employee_category(" +
                "    id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "    name_employee_category    varchar(255) not null unique" +
                ");";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateEmployeeCategoryTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create employee_category table");
            throwables.printStackTrace();
        }

        String sqlCreateEmployeeCategoryTypeTable = "create table employee_category_type ( " +
                "id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name_employee_category_type    varchar(255) not null unique, " +
                "employee_category_id INTEGER, " +
                "FOREIGN KEY (employee_category_id) REFERENCES employee_category(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlCreateEmployeeCategoryTypeTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't create employee_category_type table");
            throwables.printStackTrace();
        }
        // add many-many tables
    }

    public void insertInfo() {
        insertEmployeeCategory("Рабочий");
        insertEmployeeCategory("Инжернерно-технический персонал");
        insertEmployeeCategory("Сотрудник лаборатории");

        insertEmployeeCategoryType("Слесарь", 1);
        insertEmployeeCategoryType("Инженер", 2);
        insertEmployeeCategoryType("Лаборант", 3);
    }

    public void insertEmployeeCategory (String name) {
        PreparedStatement preparedStatement = null;
        String sqlInsertEmployeeCategoryTable = "INSERT INTO employee_category(name_employee_category) " +
                "VALUES ('" + name + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertEmployeeCategoryTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into employee_category table");
            throwables.printStackTrace();
        }
    }

    public void insertEmployeeCategoryType (String name, Integer employeeCategoryId) {
        PreparedStatement preparedStatement = null;
        String sqlInsertEmployeeCategoryTable = "INSERT INTO employee_category_type(name_employee_category_type, employee_category_id) " +
                "VALUES ('" + name + "', '" + employeeCategoryId + "')";
        try {
            preparedStatement = connection.getConnection().prepareStatement(sqlInsertEmployeeCategoryTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("can't insert into employee_category_type table");
            throwables.printStackTrace();
        }
    }

    public void updateEmployeeCategory(int id, String name) {
        String sql = "UPDATE employee_category SET " +
                " name_employee_category = '" + name + "'" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category");
    }

    public void updateEmployeeCategoryType(int id, String name, Integer employeeCategoryId) {
        String sql = "UPDATE employee_category_type SET " +
                " name_employee_category_type = '" + name + "'," +
                " employee_category_id = " + employeeCategoryId + "" +
                " WHERE id = " + id;
        List<String> employee_category = new LinkedList<>();
        employee_category.add(sql);
        connection.insert(employee_category);
        System.out.println("UPDATE employee_category_type");
    }
}
