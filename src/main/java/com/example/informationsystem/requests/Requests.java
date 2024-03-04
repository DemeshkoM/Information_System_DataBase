package com.example.informationsystem.requests;

/**
 * Запросы на выборку данных (в разработке).
 *
 * @author Mikhail Demeshko
 */
public enum Requests {
    r1(new Request(
            "Здесь будут расположены запросы",
            "/ru/nsu/pharmacydatabase/windows/requests/r1.fxml")),//?Controller
    ;

    private final Request request;

    Requests(Request request) {
        this.request = request;
    }

    public String getWindowName() {
        return request.getWindowName();
    }

    public String getName() {
        return request.getName();
    }

    public static Requests getRequestByName(String name) {
        for (Requests req: values()) {
            if (req.getName().equals(name)) {
                return req;
            }
        }
        throw new IllegalArgumentException("No enum found with name: [" + name + "]");
    }
}
