package com.example.informationsystem.requests;

import java.util.HashMap;

/**
 * Запросы на выборку данных (в разработке).
 *
 * @author Mikhail Demeshko
 */
public enum Requests {
    select_patient(new Request(
            "select_patient",
            "/com/example/informationsystem/windows/cashier/cashier_search_patient.fxml",
            new HashMap<String,String>(){{
                put("id", "ID");
                put("full_name_patient", "ФИО пациента");
                put("phone_number", "Номер телефона");
                put("address", "Адрес");
                put("date_of_birth", "Дата рождения");
            }})),

    select_doctor(new Request(
            "select_doctor",
            "/com/example/informationsystem/windows/cashier/cashier_search_doctor.fxml",
            new HashMap<String,String>(){{
                put("id", "ID");
                put("full_name_doctor", "ФИО доктора");
            }})),
    select_prescription(new Request(
            "select_prescription",
            "/com/example/informationsystem/windows/cashier/cashier_search_prescription.fxml",
            new HashMap<String,String>(){{
                put("id", "ID");
                put("full_name_patient", "ФИО пациента");
                put("name_med_diagnosis_description", "Область применения лек-ва");
                put("name_medicament", "Название лек-ва");
                put("full_name_doctor", "ФИО доктора");
                put("amount", "Кол-во требуемого лек-ва");
                put("diagnosis", "Диагноз из рецепта");
                put("direction_for_use", "Способ применения из рецепта");
                put("start_date", "Дата открытия заказа");
                put("end_date", "Дата закрытия заказа");
                put("sales_date", "Дата оплаты заказа");
                put("name_order_status", "Текущий статус заказа");
            }})),
    select_ready_med(new Request(
            "select_ready_med",
            "/com/example/informationsystem/windows/cashier/cashier_search_ready_med.fxml",
    new HashMap<String,String>(){{
        put("id", "ID");
        put("name_medicament", "Название лек-ва");
        put("selling_without_presc", "Продается ли лек-во без рецепта?");
        put("name_med_type", "Тип лек-ва");
        put("name_med_diagnosis_description", "Область применения лек-ва");
        put("stock_quantity", "Кол-во лек-ва на складе");
        put("critical_quantity", "Критическая норма");
        put("price", "Цена лек-ва");
    }})),
    select_ingredient(new Request(
            "select_ingredient",
            "/com/example/informationsystem/windows/provider/provider_search_ingredient.fxml",
            new HashMap<String,String>(){{
                put("id", "ID");
                put("name_ingredient", "Название ингредиента");
                put("stock_quantity", "Кол-во ингр-та на складе");
                put("critical_quantity", "Критическая норма");
                put("price", "Цена ингр-та");
            }})),
    select_recipe(new Request(
            "select_recipe",
            "/com/example/informationsystem/windows/provider/provider_search_recipe.fxml",
            new HashMap<String,String>(){{
                put("id", "ID");
                put("name_medicament", "Название лек-ва");
                put("name_med_type", "Тип лек-ва");
                put("name_med_diagnosis_description", "Область применения лек-ва");
                put("preparation_method", "Инструкция по приготовлению лек-ва");
                put("time_hours", "Время приготовления в часах");
                put("total_price", "Цена лек-ва");
            }}))
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
    public String getColumnName(String index) {return request.getColumnName(index);}

    public static Requests getRequestByName(String name) {
        for (Requests req: values()) {
            if (req.getName().equals(name)) {
                return req;
            }
        }
        throw new IllegalArgumentException("No enum found with name: [" + name + "]");
    }
}
