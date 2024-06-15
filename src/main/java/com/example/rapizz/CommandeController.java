package com.example.rapizz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
    private TableColumn<PizzaOrder, Double> colPrixTotal;

    @FXML
    private TableColumn<PizzaOrder, Integer> colQuantite;

    @FXML
    private Button btnCompte;

    private List<PizzaOrder> orders = new ArrayList<>();

    private double prixTotal = 0.0;

    private int quantiteTot = 0;

    @FXML
    private TextField textFieldAdresse;

    @FXML
    private TextField textFieldComplementAdresse;

    @FXML
    private TextField textFieldTelephone;

    @FXML
    private Button buttonCommander;

    @FXML
    private Button btnHistorique;

    @FXML
    private Label labelPrixTotal;

    @FXML
    private Label nameClient;

    @FXML
    private Label soldeClient;

    private User user;
    private int nbPizzaCommande;
    private Connection cnx;

    public CommandeController(User user, Connection cnx) {
        this.user = user;
        this.cnx = cnx;
    }

    @FXML
    private void initialize() throws SQLException {

        this.nbPizzaCommande = user.getNbPizzaBuy();

        buttonCommander.setOnAction(event -> handleCommander());
        btnCompte.setOnAction(event -> handleCompteClick());
        btnHistorique.setOnAction(event -> handleHistoriqueCommande());

        colNomPizza.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTaille.setCellValueFactory(new PropertyValueFactory<>("size"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrixTotal.setCellValueFactory(new PropertyValueFactory<>("priceTotal"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        ObservableList<Pizza> pizzas = getPizzasFromDatabase();

        for (Pizza pizza : pizzas) {
            vBoxPizzas.getChildren().add(createPizzaBox(pizza, pizza.name, pizza.ingredientList));
        }

        getAdressePhoneUser();

        nameClient.setText(user.toString());

        soldeClient.setText(user.showSolde());

        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));
    }

    private void handleCompteClick() {
        // Créez une boîte de dialogue
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Solde du compte");
        alert.setHeaderText(null);

        // Créez une GridPane pour contenir le champ de saisie
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Créez le champ de saisie
        TextField textField = new TextField();
        textField.setPromptText("Entrez le montant à ajouter au solde");

        // Ajoutez le champ de saisie à la GridPane
        gridPane.add(textField, 0, 0);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        // Ajoutez la GridPane au contenu de la boîte de dialogue
        alert.getDialogPane().setContent(gridPane);

        // Attendez que l'utilisateur ferme la boîte de dialogue
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Traitez le texte entré par l'utilisateur
                String inputText = textField.getText();
                // Check si il n'y a qur des chiffres dans le input text
                if (inputText.matches("[0-9]+")) {
                    try {
                        double montant = Double.parseDouble(inputText);
                        user.updateSolde(montant, cnx);
                        soldeClient.setText(user.showSolde());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Erreur de saisie");
                    alertError.setHeaderText(null);
                    alertError.setContentText("Veuillez entrer un montant valide.");
                    alertError.showAndWait();
                }
                System.out.println("Texte entré : " + inputText);
            }
        });
    }

    private void handleHistoriqueCommande() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historique des commandes");
        alert.setHeaderText(null);

        // Utilisation de TextArea pour afficher l'historique
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Query pour récupérer les commandes
        String query = "SELECT c.id_commande, c.date_commande, c.date_start_livraison, c.date_end_livraison, c.quantite, c.prix_total " +
                "FROM commande AS c " +
                "WHERE c.id_client = 3 " +
                "ORDER BY c.date_commande DESC;";

        StringBuilder historique = new StringBuilder("Voici l'historique de vos commandes :\n\n");

        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int idCommande = rs.getInt("id_commande");
                Date dateCommande = rs.getDate("date_commande");
                Date dateStartLivraison = rs.getDate("date_start_livraison");
                Date dateEndLivraison = rs.getDate("date_end_livraison");
                int quantite = rs.getInt("quantite");
                double prixTotal = rs.getDouble("prix_total");

                historique.append("Commande n°").append(idCommande)
                        .append(" du ").append(dateCommande)
                        .append(" : ").append(quantite)
                        .append(" pizzas pour un total de ").append(prixTotal).append("€\n");

                String query2 = "SELECT pv.nom_pizza, pd.type, pd.quantite " +
                        "FROM produitsvendus AS pd JOIN pizzavendu AS pv ON pd.id_produits_vendus = pv.id_produits_vendus " +
                        "WHERE pd.id_commande = ?";

                try (PreparedStatement pstmt = cnx.prepareStatement(query2)) {
                    pstmt.setInt(1, idCommande);
                    try (ResultSet rs2 = pstmt.executeQuery()) {
                        while (rs2.next()) {
                            String nomPizza = rs2.getString("nom_pizza");
                            String type = rs2.getString("type");
                            int quantitePizza = rs2.getInt("quantite");

                            historique.append("  - ").append(quantitePizza).append(" ").append(type).append(" ").append(nomPizza).append("\n");
                        }
                    }
                }
                historique.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            historique.append("Erreur lors de la récupération de l'historique des commandes.");
        }

        textArea.setText(historique.toString());

        GridPane gridPane = new GridPane();
        gridPane.add(textArea, 0, 0);

        alert.getDialogPane().setContent(gridPane);

        alert.showAndWait();
    }

    private void getAdressePhoneUser() {
        textFieldAdresse.setText(user.getAdresse());
        textFieldComplementAdresse.setText(user.getComplementAdresse());
        textFieldTelephone.setText(String.valueOf(user.getTelephone()));
    }

    private VBox createPizzaBox(Pizza piz, String pizzaName, String ingredient) {
        VBox pizzaBox = new VBox(10);
        pizzaBox.setAlignment(Pos.CENTER);
        pizzaBox.setStyle("-fx-border-color: #4a6cf3; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 10;");
        pizzaBox.setPrefWidth(200);
        pizzaBox.setPrefHeight(150);

        Label labelName = new Label(pizzaName);
        ComboBox<String> comboBoxTaille = new ComboBox<>();
        comboBoxTaille.setPromptText("Taille");
        comboBoxTaille.getItems().addAll("Naine", "Humaine", "Ogresse");
        Text textIngredient = new Text("Ingrédient : " + ingredient);
        textIngredient.setWrappingWidth(180);  // Ajustez la largeur selon vos besoins
        textIngredient.setTextAlignment(TextAlignment.CENTER);
        Button buttonAdd = new Button("Ajoutée");
        buttonAdd.setOnAction(event -> handleAddPizza(piz, pizzaName, comboBoxTaille.getValue()));

        pizzaBox.getChildren().addAll(labelName, comboBoxTaille, textIngredient, buttonAdd);
        return pizzaBox;
    }

    private void handleAddPizza(Pizza piz, String pizzaName, String size) {
        if (size == null || size.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune taille sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une taille pour " + pizzaName);
            alert.showAndWait();
            return;
        }

        double price = calculatePrice(size, piz.getPrice());
        if (nbPizzaCommande % 10 == 0 && nbPizzaCommande != 0) { // A chaque 10ème pizza commandée, la 11ème est offerte
            price = 0;
        }
        for (PizzaOrder order : orders) {
            if (order.nameProperty().get().equals(pizzaName) && order.sizeProperty().get().equals(size)) {
                order.quantityProperty().set(order.quantityProperty().get() + 1);
                order.priceTotalProperty().set(order.priceTotalProperty().get() + price);
                prixTotal += price;
                quantiteTot += 1;
                nbPizzaCommande += 1;
                labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));
                return;
            }
        }

        PizzaOrder order = new PizzaOrder(pizzaName, size, price, price, 1);
        tableViewRecap.getItems().add(order);
        orders.add(order);
        quantiteTot += 1;
        nbPizzaCommande += 1;
        prixTotal += price;
        labelPrixTotal.setText(String.format("Prix total: %.2f €", prixTotal));

    }

    private void handleCommander() {

        if (user.getSolde() < prixTotal) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Solde insuffisant");
            alert.setHeaderText(null);
            alert.setContentText("Votre solde est insuffisant pour effectuer cette commande.");
            alert.showAndWait();
            return;
        }
        boolean commit = true;
        try {
            cnx.setAutoCommit(false);

            int orderId = insertOrder();

            for (PizzaOrder pizzaO : orders) {
                String pizzaName = pizzaO.nameProperty().get();
                String size = pizzaO.sizeProperty().get();
                double price = pizzaO.priceProperty().get();
                double priceTotal = pizzaO.priceTotalProperty().get();
                int quantity = pizzaO.quantityProperty().get();

                int productId = insertProductSold(orderId, quantity, price, size);
                insertPizzaSold(productId, pizzaName);
            }

            cnx.commit();
        } catch (SQLException e) {
            if (cnx != null) {
                try {
                    commit = false;
                    cnx.rollback();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur lors de la commande");
                    alert.setHeaderText(null);
                    alert.setContentText("Une erreur est survenue lors de l'enregistrement de votre commande. Veuillez réessayer plus tard.");
                    alert.showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (commit) {
                try {
                    cnx.setAutoCommit(true);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Commande effectuée");
                    alert.setHeaderText(null);
                    alert.setContentText("Votre commande a été enregistrée. Elle sera livrée dans les plus brefs délais !");
                    alert.showAndWait();
                    user.updateSolde(-prixTotal, cnx);
                    soldeClient.setText(user.showSolde());
                    orders.clear();
                    tableViewRecap.getItems().clear();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private double calculatePrice(String size, double basePrice) {
        switch (size) {
            case "Naine":
                return 0.66 * basePrice;
            case "Humaine":
                return basePrice;
            case "Ogresse":
                return 1.33 * basePrice;
            default:
                return 0.0;
        }
    }

    private ObservableList<Pizza> getPizzasFromDatabase() throws SQLException {
        ObservableList<Pizza> pizzas = FXCollections.observableArrayList();
        String pizzaQuery = "SELECT nom_pizza, base_prix FROM pizza";
        String ingredientQuery = "SELECT nom_ingredient FROM composer WHERE nom_pizza = ?";



             Statement stmt = cnx.createStatement();
             try (ResultSet rsPizza = stmt.executeQuery(pizzaQuery)) {

            while (rsPizza.next()) {
                String pizzaName = rsPizza.getString("nom_pizza");
                double basePrice = rsPizza.getDouble("base_prix");
                List<String> ingredients = new ArrayList<>();

                try (PreparedStatement pstmt = cnx.prepareStatement(ingredientQuery)) {
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

    private int getLivreurDisponible() throws SQLException {
        String sql = "SELECT l.id_livreur, l.nom, l.prenom " +
                "FROM livreur as l " +
                "JOIN commande AS c ON l.id_livreur = c.id_livreur " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM commande AS c2 " +
                "    WHERE c2.id_livreur = l.id_livreur AND c2.statut = 0 " +
                ") " +
                "GROUP BY l.id_livreur, l.nom, l.prenom " +
                "LIMIT 1;";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id_livreur");
            }
        }
        return -1;
    }

    private int insertOrder() throws SQLException {
        String sql = "INSERT INTO commande(" +
                "date_commande, date_start_livraison, date_end_livraison, prix_total, statut, quantite, id_livreur, id_client, adresse, complement_adresse, telephone" +
                ") " +
                "VALUES(NOW(),DATE_ADD(NOW(), INTERVAL 10 MINUTE),NULL,?,0,?,?,?,?,?,?" +
                ")";
        int idLivreur = getLivreurDisponible();

        try (PreparedStatement pstmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, prixTotal);
            pstmt.setInt(2, quantiteTot);
            if (idLivreur == -1) {
                pstmt.setNull(3, Types.INTEGER);
            }
            else {
                pstmt.setInt(3, idLivreur);
            }
            pstmt.setInt(4, user.getIdUser());
            pstmt.setString(5, textFieldAdresse.getText());
            pstmt.setString(6, textFieldComplementAdresse.getText());
            pstmt.setInt(7, Integer.parseInt(textFieldTelephone.getText()));
            //System.out.println(pstmt);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private int insertProductSold(int orderId, int quantity, double unitPrice, String type) throws SQLException {
        String sql = "INSERT INTO produitsvendus(id_commande, quantite, prix_unitaire, prix_total, type) VALUES(?,?,?,?,?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitPrice);
            pstmt.setDouble(4, unitPrice * quantity);
            pstmt.setString(5, type);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private void insertPizzaSold(int productId, String pizzaName) throws SQLException {
        String sql = "INSERT INTO pizzavendu(nom_pizza, id_produits_vendus) VALUES(?,?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, pizzaName);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        }
    }

}
