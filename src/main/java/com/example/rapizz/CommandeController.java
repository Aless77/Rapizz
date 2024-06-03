package com.example.rapizz;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CommandeController {

    @FXML
    private VBox vBoxPizzas;

    @FXML
    private TableView<PizzaOrder> tableViewRecap;

    @FXML
    private TableColumn<PizzaOrder, String> colNomPizza;

    @FXML
    private TableColumn<PizzaOrder, String> colTaille;

    @FXML
    private TableColumn<PizzaOrder, Double> colPrixUnitaire;

    @FXML
    private TextField textFieldAdresse;

    @FXML
    private TextField textFieldComplementAdresse;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private Label labelPrixTotal;

    private double prixTotal = 0.0;

    @FXML
    private void initialize() {
        // Initialiser les colonnes pour utiliser les propriétés de PizzaOrder
        colNomPizza.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colTaille.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        colPrixUnitaire.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        // Ajouter les pizzas à la VBox (comme montré précédemment)
        String[] pizzasSauceTomate = {"Pizza Margherita", "Pizza Pepperoni", "Pizza Veggie", "Pizza Hawaïenne"};
        String[] pizzasCremeFraiche = {"Pizza Carbonara", "Pizza BBQ Chicken", "Pizza 4 Fromages", "Pizza Saumon"};

        for (String pizza : pizzasSauceTomate) {
            vBoxPizzas.getChildren().add(createPizzaBox(pizza, "sauce tomate"));
        }

        for (String pizza : pizzasCremeFraiche) {
            vBoxPizzas.getChildren().add(createPizzaBox(pizza, "crème fraîche"));
        }

        // Set default address values
        textFieldAdresse.setText("123 Rue de l'Exemple");
        textFieldComplementAdresse.setText("Appartement 4B");
        textFieldTelephone.setText("0123456789");

        // Initialize prix total
        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));
    }

    private VBox createPizzaBox(String pizzaName, String base) {
        VBox pizzaBox = new VBox(10);
        pizzaBox.setAlignment(Pos.CENTER);
        pizzaBox.setStyle("-fx-border-color: #4a6cf3; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 10;");

        // Définir la taille préférée pour le VBox
        pizzaBox.setPrefWidth(200);  // Largeur préférée
        pizzaBox.setPrefHeight(150); // Hauteur préférée

        Label labelName = new Label(pizzaName);
        ComboBox<String> comboBoxTaille = new ComboBox<>();
        comboBoxTaille.setPromptText("Taille"); // Définir le texte "Taille"
        comboBoxTaille.getItems().addAll("Naine", "Humaine", "Ogresse");
        Label labelBase = new Label("Base : " + base);
        Button buttonAdd = new Button("Ajoutée");
        buttonAdd.setOnAction(event -> handleAddPizza(pizzaName, comboBoxTaille.getValue()));

        pizzaBox.getChildren().addAll(labelName, comboBoxTaille, labelBase, buttonAdd);
        return pizzaBox;
    }

    private void handleAddPizza(String pizzaName, String size) {
        if (size == null || size.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune taille sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une taille pour " + pizzaName);
            alert.showAndWait();
            return;
        }

        double price = calculatePrice(size);

        PizzaOrder order = new PizzaOrder(pizzaName, size, price);
        tableViewRecap.getItems().add(order);

        // Update total price
        prixTotal += price;
        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));
    }

    private double calculatePrice(String size) {
        switch (size) {
            case "Naine":
                return 8.0;
            case "Humaine":
                return 10.0;
            case "Ogresse":
                return 12.0;
            default:
                return 0.0;
        }
    }
}
