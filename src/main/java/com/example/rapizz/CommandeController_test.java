package com.example.rapizz;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CommandeController_test {

    @FXML
    private ComboBox<String> comboBoxPizza1;

    @FXML
    private ComboBox<String> comboBoxTaille1;

    @FXML
    private ComboBox<String> comboBoxPizza2;

    @FXML
    private ComboBox<String> comboBoxTaille2;

    @FXML
    private TableView<?> tableViewRecap;

    @FXML
    private TextField textFieldAdresse;

    @FXML
    private TextField textFieldComplementAdresse;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private void initialize() {
        // Initialize combo boxes with sample data
        comboBoxPizza1.getItems().addAll("Pizza Margherita", "Pizza Pepperoni");
        comboBoxTaille1.getItems().addAll("Small", "Medium", "Large");
        comboBoxPizza2.getItems().addAll("Pizza Carbonara", "Pizza BBQ Chicken");
        comboBoxTaille2.getItems().addAll("Small", "Medium", "Large");
    }

    @FXML
    private void handleAddPizza1() {
        // Add pizza 1 to the recap table
    }

    @FXML
    private void handleAddPizza2() {
        // Add pizza 2 to the recap table
    }

    @FXML
    private void handleCommander() {
        // Handle the command action
    }
}
