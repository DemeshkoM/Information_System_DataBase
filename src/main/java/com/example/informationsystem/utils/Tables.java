package com.example.informationsystem.utils;

public enum Tables {
    employee_category(new Table(
            "employee_category",
            "/com/example/informationsystem/windows/insert/employee_category.fxml"
    )),

    employee_category_type(new Table(
       "employee_category_type",
       "/com/example/informationsystem/windows/insert/employee_category_type.fxml"
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
