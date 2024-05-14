package com.example.informationsystem.utils;

import java.util.HashMap;

/**
 * Выполняет связь окон с названием таблиц
 *
 * @author Mikhail Demeshko
 */
public class Table {
    private final String name;
    private final String windowName;
    private final HashMap<Integer, String> columnNames;

    Table(String name, String windowName, HashMap columnNames) {
        this.name = name;
        this.windowName = windowName;
        this.columnNames = columnNames;
    }

    String getName() {
        return name;
    }

    String getWindowName() {
        return windowName;
    }

    String getColumnName(Integer index) {return columnNames.get(index);}
}
