package com.example.informationsystem.requests;

/**
 * Запросы на выборку данных (в разработке).
 *
 * @author Mikhail Demeshko
 */
public class Request {
    private final String name;
    private final String windowName;

    Request(String name, String windowName) {
        this.name = name;
        this.windowName = windowName;
    }

    String getName()
    {
        return name;
    }

    String getWindowName()
    {
        return windowName;
    }
}
