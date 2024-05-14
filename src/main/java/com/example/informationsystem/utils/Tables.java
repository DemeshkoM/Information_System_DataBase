package com.example.informationsystem.utils;

import java.util.HashMap;

public enum Tables {
    medication_category(new Table(
            "medication_category",
            "/com/example/informationsystem/windows/insert/med_category.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "Название категории лек-ва");
            }}
    )),

    medication_types(new Table(
       "medication_types",
       "/com/example/informationsystem/windows/insert/med_types.fxml",
               new HashMap<Integer,String>(){{
                   put(1, "ID");
                   put(2, "Название типа лек-ва");
                   put(3, "ID категории лек-ва");
               }}
    )),

    medicine(new Table(
            "medicine",
       "/com/example/informationsystem/windows/insert/medicine.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "Название лек-ва");
                put(3, "ID типа лек-ва");
                put(4, "ID области применения лек-ва");
                put(5, "Продается ли лек-во без рецепта?");
            }}
    )),

    ready_medicine(new Table(
            "ready_medicine",
            "/com/example/informationsystem/windows/insert/ready_medicine.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "ID типа лек-ва");
                put(3, "Кол-во лек-ва на складе");
                put(4, "Критическая норма");
                put(5, "Цена лек-ва");
            }}
    )),

    doctor(new Table(
            "doctor",
            "/com/example/informationsystem/windows/insert/doctor.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "ФИО");
            }}
    )),

    patient(new Table(
            "patient",
            "/com/example/informationsystem/windows/insert/patient.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "ФИО");
                put(3, "Номер телефона");
                put(4, "Адрес");
                put(5, "Дата рождения");
            }}
    )),

    prescription(new Table(
            "prescription",
            "/com/example/informationsystem/windows/insert/prescription.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "ID пациента");
                put(3, "ID лек-ва");
                put(4, "ID доктора");
                put(5, "Кол-во на продажу");
                put(6, "Диагноз из рецепта");
                put(7, "Способ применения из рецепта");
            }}
    )),

    production_order(new Table(
            "production_order",
            "/com/example/informationsystem/windows/insert/production_order.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID заказа");
                put(2, "ID статуса");
                put(3, "Дата открытия заказа");
                put(4, "Дата закрытия заказа");
            }}
    )),

    order_status(new Table(
            "order_status",
                    "/com/example/informationsystem/windows/insert/order_status.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "Название статуса");
            }}
    )),

    sales(new Table(
            "sales",
            "/com/example/informationsystem/windows/insert/sales.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID заказа");
                put(2, "Дата оплаты заказа");
            }}
    )),

    recipe(new Table(
            "recipe",
            "/com/example/informationsystem/windows/insert/recipe.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "Инструкция по приготовлению лек-ва");
                put(3, "Время приготовления в часах");
            }}
    )),
    ingredient(new Table(
            "ingredient",
            "/com/example/informationsystem/windows/insert/ingredient.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID");
                put(2, "Название ингредиента");
                put(3, "Кол-во ингр-та на складе");
                put(4, "Критическая норма");
                put(5, "Цена ингр-та");
            }}
    )),
    ingredient_recipe(new Table(
            "ingredient_recipe",
            "/com/example/informationsystem/windows/insert/recipe_ingredient.fxml",
            new HashMap<Integer,String>(){{
                put(1, "ID рецепта");
                put(2, "ID ингредиента");
                put(3, "Кол-во расходуемого ингр-та в ходе приготовления");
            }}
    )),
    medication_diagnosis_description(new Table(
            "medication_diagnosis_description",
            "/com/example/informationsystem/windows/insert/med_diagnosis_desc.fxml",
            new HashMap<Integer,String>(){{
        put(1, "ID");
        put(2, "Область применения лек-ва");
    }}
    )),
    view_search_prescription_end_date(new Table(
       "view_search_prescription_end_date",
            "/com/example/informationsystem/windows/cashier/cashier_update_end_date.fxml", new HashMap<Integer,String>()
    )),
    view_search_prescription_sales_date(new Table(
            "view_search_prescription_sales_date",
            "/com/example/informationsystem/windows/cashier/cashier_update_sales_date.fxml", new HashMap<Integer,String>()
    )),
    view_search_prescription_order_status(new Table(
            "view_search_prescription_order_status",
            "/com/example/informationsystem/windows/cashier/cashier_update_order_status.fxml", new HashMap<Integer,String>()
    )),
    view_search_prescription_second_attributes(new Table(
            "view_search_prescription_second_attributes",
            "/com/example/informationsystem/windows/cashier/cashier_update_prescription_second_attributes.fxml", new HashMap<Integer,String>()
    )),
    view_search_recipe_medicine(new Table(
            "view_search_recipe_medicine",
            "/com/example/informationsystem/windows/provider/provider_update_medicine.fxml", new HashMap<Integer,String>()
    )),
    view_search_recipe_recipe(new Table(
            "view_search_recipe_recipe",
            "/com/example/informationsystem/windows/provider/provider_update_recipe.fxml", new HashMap<Integer,String>()
    ));


    private Table table;

    Tables(Table table) {
        this.table = table;
    }

    public String getWindowName() {
        return table.getWindowName();
    }

    String getName() {
        return table.getName();
    }

    public String getColumnName(Integer index) {return table.getColumnName(index);}

    public static Tables getTableByName(String name) {
        for (Tables table: values()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        throw new IllegalArgumentException("No enum found with name: [" + name + "]");
    }
}
