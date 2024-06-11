package com.example.rapizz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        colNomPizza.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTaille.setCellValueFactory(new PropertyValueFactory<>("size"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("price"));

        ObservableList<Pizza> pizzas = getPizzasFromDatabase();

        for (Pizza pizza : pizzas) {
            vBoxPizzas.getChildren().add(createPizzaBox(pizza.name, pizza.ingredientList));
        }

        textFieldAdresse.setText("123 Rue de l'Exemple");
        textFieldComplementAdresse.setText("Appartement 4B");
        textFieldTelephone.setText("0123456789");

        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));
    }

    private VBox createPizzaBox(String pizzaName, String ingredient) {
        VBox pizzaBox = new VBox(10);
        pizzaBox.setAlignment(Pos.CENTER);
        pizzaBox.setStyle("-fx-border-color: #4a6cf3; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 10;");
        pizzaBox.setPrefWidth(200);
        pizzaBox.setPrefHeight(150);

        Label labelName = new Label(pizzaName);
        ComboBox<String> comboBoxTaille = new ComboBox<>();
        comboBoxTaille.setPromptText("Taille");
        comboBoxTaille.getItems().addAll("Naine", "Humaine", "Ogresse");
        Label labelIngredient = new Label("Ingrédient : " + ingredient);
        Button buttonAdd = new Button("Ajoutée");
        buttonAdd.setOnAction(event -> handleAddPizza(pizzaName, comboBoxTaille.getValue()));

        pizzaBox.getChildren().addAll(labelName, comboBoxTaille, labelIngredient, buttonAdd);
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

        prixTotal += price;
        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));

        Connection conn = null;

        try {
            conn = new DataBaseConnect().connect();
            conn.setAutoCommit(false);

            int orderId = insertOrder(conn, textFieldAdresse.getText(), textFieldComplementAdresse.getText(), textFieldTelephone.getText(), prixTotal);
            int pizzaId = getPizzaId(conn, pizzaName);
            insertProductSold(conn, orderId, pizzaId, 1, price, size);
            insertPizzaSold(conn, orderId, pizzaId, pizzaName);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private ObservableList<Pizza> getPizzasFromDatabase() {
        ObservableList<Pizza> pizzas = FXCollections.observableArrayList();
        String pizzaQuery = "SELECT nom_pizza, base_prix FROM pizza";
        String ingredientQuery = "SELECT nom_ingredient FROM composer WHERE nom_pizza = ?";

        DataBaseConnect dbConnect = new DataBaseConnect();

        try (Connection conn = dbConnect.connect();
             Statement stmt = conn.createStatement();
             ResultSet rsPizza = stmt.executeQuery(pizzaQuery)) {

            while (rsPizza.next()) {
                String pizzaName = rsPizza.getString("nom_pizza");
                double basePrice = rsPizza.getDouble("base_prix");
                List<String> ingredients = new ArrayList<>();

                try (PreparedStatement pstmt = conn.prepareStatement(ingredientQuery)) {
                    pstmt.setString(1, pizzaName);
                    try (ResultSet rsIngredient = pstmt.executeQuery()) {
                        while (rsIngredient.next()) {
                            ingredients.add(rsIngredient.getString("nom_ingredient"));
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while retrieving ingredients for pizza: " + pizzaName);
                    e.printStackTrace();
                }

                String ingredientList = String.join(", ", ingredients);
                Pizza pizza = new Pizza(pizzaName, basePrice, ingredientList);
                pizzas.add(pizza);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving pizzas from the database. \nError: " + e.getMessage());
        }

        return pizzas;
    }

    private int insertOrder(Connection conn, String adresse, String complement, String telephone, double totalPrice) throws SQLException {
        String sql = "INSERT INTO commande(adresse, complement, telephone, prix_total, date_commande, date_start_livraison, date_end_livraison, statut, quantite, id_client) VALUES(?,?,?,?,NOW(),NOW(),NOW(),0,1,1)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, adresse);
            pstmt.setString(2, complement);
            pstmt.setString(3, telephone);
            pstmt.setDouble(4, totalPrice);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private void insertProductSold(Connection conn, int orderId, int productId, int quantity, double unitPrice, String type) throws SQLException {
        String sql = "INSERT INTO produitsvendus(id_commande, quantite, prix_unitaire, prix_total, type) VALUES(?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitPrice);
            pstmt.setDouble(4, unitPrice * quantity);
            pstmt.setString(5, type);
            pstmt.executeUpdate();
        }
    }

    private void insertPizzaSold(Connection conn, int orderId, int productId, String pizzaName) throws SQLException {
        String sql = "INSERT INTO pizzavendu(nom_pizza, id_produits_vendus) VALUES(?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pizzaName);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        }
    }

    private int getPizzaId(Connection conn, String pizzaName) throws SQLException {
        String sql = "SELECT id_pizza FROM pizza WHERE nom_pizza = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pizzaName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_pizza");
            }
        }
        return -1;
    }

    public static class Pizza {
        private final String name;
        private final double price;
        private final String ingredientList;

        public Pizza(String name, double price, String ingredientList) {
            this.name = name;
            this.price = price;
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

    public static class PizzaOrder {
        private final String name;
        private final String size;
        private final double price;

        public PizzaOrder(String name, String size, double price) {
            this.name = name;
            this.size = size;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public double getPrice() {
            return price;
        }
    }
}
