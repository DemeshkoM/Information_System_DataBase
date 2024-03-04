package com.example.informationsystem.utils;
/**
 * Выполняет связь окон с названием таблиц
 *
 * @author Mikhail Demeshko
 */
public class Table {
    private final String name;
    private final String windowName;

    Table(String name, String windowName) {
        this.name = name;
        this.windowName = windowName;
    }

    String getName() {
        return name;
    }

    String getWindowName() {
        return windowName;
    }
}
