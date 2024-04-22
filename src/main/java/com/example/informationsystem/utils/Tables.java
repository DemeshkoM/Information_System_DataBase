package com.example.informationsystem.utils;

public enum Tables {
    medication_category(new Table(
            "medication_category",
            "/com/example/informationsystem/windows/insert/med_category.fxml"
    )),

    medication_types(new Table(
       "medication_types",
       "/com/example/informationsystem/windows/insert/med_types.fxml"
    )),

    medicine(new Table(
            "medicine",
       "/com/example/informationsystem/windows/insert/medicine.fxml"
    )),

    ready_medicine(new Table(
            "ready_medicine",
            "/com/example/informationsystem/windows/insert/ready_medicine.fxml"
    )),

    doctor(new Table(
            "doctor",
            "/com/example/informationsystem/windows/insert/doctor.fxml"
    )),

    patient(new Table(
            "patient",
            "/com/example/informationsystem/windows/insert/patient.fxml"
    )),

    prescription(new Table(
            "prescription",
            "/com/example/informationsystem/windows/insert/prescription.fxml"
    )),

    production_order(new Table(
            "production_order",
            "/com/example/informationsystem/windows/insert/production_order.fxml"
    )),

    order_status(new Table(
            "order_status",
                    "/com/example/informationsystem/windows/insert/order_status.fxml"
    )),

    sales(new Table(
            "sales",
            "/com/example/informationsystem/windows/insert/sales.fxml"
    )),

    recipe(new Table(
            "recipe",
            "/com/example/informationsystem/windows/insert/recipe.fxml"
    )),
    ingredient(new Table(
            "ingredient",
            "/com/example/informationsystem/windows/insert/ingredient.fxml"
    )),
    ingredient_recipe(new Table(
            "ingredient_recipe",
            "/com/example/informationsystem/windows/insert/recipe_ingredient.fxml"
    )),
    medication_diagnosis_description(new Table(
            "medication_diagnosis_description",
            "/com/example/informationsystem/windows/insert/med_diagnosis_desc.fxml"
    )),
    view_search_prescription_end_date(new Table(
       "view_search_prescription_end_date",
            "/com/example/informationsystem/windows/cashier/cashier_update_end_date.fxml"
    )),
    view_search_prescription_sales_date(new Table(
            "view_search_prescription_sales_date",
            "/com/example/informationsystem/windows/cashier/cashier_update_sales_date.fxml"
    )),
    view_search_prescription_order_status(new Table(
            "view_search_prescription_order_status",
            "/com/example/informationsystem/windows/cashier/cashier_update_order_status.fxml"
    )),
    view_search_prescription_second_attributes(new Table(
            "view_search_prescription_second_attributes",
            "/com/example/informationsystem/windows/cashier/cashier_update_prescription_second_attributes.fxml"
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

    public static Tables getTableByName(String name) {
        for (Tables table: values()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        throw new IllegalArgumentException("No enum found with name: [" + name + "]");
    }
}
