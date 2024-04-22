package com.example.informationsystem.utils;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * Осуществвляет соединение и взаимодествие с сервером БД
 *
 * @author Mikhail Demeshko
 */
public class Connection {
    private java.sql.Connection connection;
    private static final String localIP = "127.0.0.1";
    private static final String defaultPort = "3306";
    private String url = "jdbc:mysql://127.0.0.1:3306/";
    public String dataBaseName;
    public Connection() {
    }

    public java.sql.Connection getConnection() {
        return connection;
    }

    public void registerConnection(String loginText, String passwordText,
    String hostText, String portText) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println(url);
        createConnection();
        System.out.println("register connection");

        if(hostText != localIP || portText != defaultPort) {
            url = "jdbc:mysql://" + hostText + ":" + portText + "/";
        }

        connection = DriverManager.getConnection(url, loginText, passwordText);

        Statement s1 = connection.createStatement();
        s1.executeUpdate("CREATE DATABASE IF NOT EXISTS DBname;");
        s1.close();

        Statement s2 = connection.createStatement();

        s2.executeUpdate("USE DBname");

        s2.close();

        if (connection.isValid(1)) {
            System.out.println("success connection");
        } else {
            System.out.println("bad connection");
        }
    }

    private void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
        TimeZone.setDefault(timeZone);
        Locale.setDefault(Locale.ENGLISH);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("connection was closed");
        } else {
            System.out.println("connection is not registered");
        }
    }

    public ResultSet executeQueryAndGetResult(String sql) {
        createConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //preparedStatement.executeUpdate(sql);
            return preparedStatement.executeQuery();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public void executeQuery(String sql) throws SQLException {
        createConnection();
        try(PreparedStatement preStatement = connection.prepareStatement(sql)) {
            preStatement.executeUpdate(sql);
        }
    }

    public void insert(List<String> queryList) {
        createConnection();
        for(String query : queryList) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<List<String>> select(String sql) {
        return select(sql, result -> {
            try {
                ArrayList<String> list = new ArrayList<>(1);
                list.add(result.getString(1));
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public List<List<String>> select(String sql, Function<ResultSet, List<String>> toString) {
        createConnection();
        List<List<String>> names = new LinkedList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                names.add(toString.apply(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public void delete(String slq) {
        createConnection();
        try (PreparedStatement statement = connection.prepareStatement(slq)) {
            statement.executeUpdate(slq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}