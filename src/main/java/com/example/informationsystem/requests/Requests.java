package com.example.informationsystem.requests;

/**
 * Запросы на выборку данных (в разработке).
 *
 * @author Mikhail Demeshko
 */
public enum Requests {
    select_patient(new Request(
            "select_patient",
            "/com/example/informationsystem/windows/cashier/cashier_search_patient.fxml")),

    select_doctor(new Request(
            "select_doctor",
            "/com/example/informationsystem/windows/cashier/cashier_search_doctor.fxml")),
    select_prescription(new Request(
            "select_prescription",
            "/com/example/informationsystem/windows/cashier/cashier_search_prescription.fxml")),
    select_ready_med(new Request(
            "select_ready_med",
            "/com/example/informationsystem/windows/cashier/cashier_search_ready_med.fxml")),
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
