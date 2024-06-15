package com.example.rapizz;

import javafx.beans.property.*;

import java.util.Map;

public class PizzaOrder {
    private final StringProperty name;
    private final StringProperty size;
    private final DoubleProperty price;
    private final DoubleProperty priceTotal;
    private final IntegerProperty quantity;

    public PizzaOrder(String name, String size, double price, double priceTotal, int quantity) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.price = new SimpleDoubleProperty(price);
        this.priceTotal = new SimpleDoubleProperty(priceTotal);
        this.quantity = new SimpleIntegerProperty(quantity);
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

    public DoubleProperty priceTotalProperty() {
        return priceTotal;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }


}
