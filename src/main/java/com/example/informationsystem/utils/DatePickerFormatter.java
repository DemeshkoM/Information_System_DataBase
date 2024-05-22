package com.example.informationsystem.utils;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DatePickerFormatter {
    private final List<String> patterns = Arrays.asList(
            "dd/MM/yyyy", "dd.MM.yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd");

    public DatePickerFormatter() {
    }

    public void setDatePickerFormatter(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    for (String pattern : patterns) {
                        try {
                            return DateTimeFormatter.ofPattern(pattern).format(date);
                        } catch (DateTimeException dte) { }
                    }
                    System.out.println("Format Error");
                }
                return "";
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    for (String pattern : patterns) {
                        try {
                            return LocalDate.parse(string, DateTimeFormatter.ofPattern(pattern));
                        } catch (DateTimeParseException dtpe) { }
                    }
                    System.out.println("Parse Error");
                }
                return null;
            }
        });
    }
}
