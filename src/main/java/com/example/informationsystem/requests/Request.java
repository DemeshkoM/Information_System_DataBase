package com.example.informationsystem.requests;

import java.util.HashMap;

/**
 * Запросы на выборку данных (в разработке).
 *
 * @author Mikhail Demeshko
 */
public class Request {
    private final String name;
    private final String windowName;

    private final HashMap<String, String> columnNames;

    Request(String name, String windowName, HashMap columnNames) {
        this.name = name;
        this.windowName = windowName;
        this.columnNames = columnNames;
    }

    String getName()
    {
        return name;
    }
    String getWindowName()
    {
        return windowName;
    }
    String getColumnName(String index) {return columnNames.get(index);}
}
