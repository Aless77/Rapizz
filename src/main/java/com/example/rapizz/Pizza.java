package com.example.rapizz;

public class Pizza {
    public String name;
    public double price;;
    public String ingredientList;

    public Pizza(String name, double basePrice, String ingredientList) {
        this.name = name;
        this.price = basePrice;
        this.ingredientList = ingredientList;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getIngredientList() {
        return ingredientList;
    }
}
