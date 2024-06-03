package com.example.rapizz;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class PizzaOrder {
    private final StringProperty name;
    private final StringProperty size;
    private final DoubleProperty price;

    public PizzaOrder(String name, String size, double price) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.price = new SimpleDoubleProperty(price);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public DoubleProperty priceProperty() {
        return price;
    }
}
