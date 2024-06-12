package com.example.rapizz;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatistiqueController {
    @FXML
    private Button deconnexionButton;

    @FXML
    private HBox clientInfoBox;

    @FXML
    private HBox livreurInfoBox;

    @FXML
    private HBox bestPizzaIngredientBox;

    @FXML
    private HBox vehiculeNoUseInfoBox;

    @FXML
    private HBox statCommandeInfoBox;

    @FXML
    private VBox vboxClientGridPane;

    @FXML
    private GridPane clientGridPane;

    @FXML
    private AnchorPane rootPane;

    private User user;

    private Connection cnx;

    @FXML
    private Label nameUser;

    @FXML
    private Label firstNameBestClient;

    @FXML
    private Label lastNameBestClient;

    @FXML
    private Label soldeDepenseBestClient;

    @FXML
    private Label nbPizzaCommanderBestClient;

    @FXML
    private Label firstnameWorstLivreur;

    @FXML
    private Label lastnameWorstLivreur;

    @FXML
    private Label nbWorstLivraison;

    @FXML
    private Label vehiculeWorstLivraison;

    @FXML
    private Label bestPizzaName;

    @FXML
    private Label bestPizzaNbVendue;

    @FXML
    private Label bestIngredientName;

    @FXML
    private Label nbCommandeParClient;

    @FXML
    private Label moyennePrixParClient;

    @FXML
    private Label labelCA;

    private ObservableList<Client> data = FXCollections.observableArrayList();


    public StatistiqueController(User user, Connection cnx) {
        this.user = user;
        this.cnx = cnx;
    }

    public void getBestClient() throws SQLException { // Le meilleur client est le client qui a commandé le plus de pizzas
        String query = "SELECT c.nom, c.prenom, c.solde, SUM(pd.quantite) as nb_pizza, SUM(cmd.prix_total) AS solde_depenser " +
                "FROM user AS c JOIN commande AS cmd ON c.id_user = cmd.id_client JOIN produitsvendus AS pd ON cmd.id_commande = pd.id_commande " +
                "GROUP BY c.id_user " +
                "ORDER BY nb_pizza DESC " +
                "LIMIT 1;";


        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    firstNameBestClient.setText(rs.getString("nom"));
                    lastNameBestClient.setText(rs.getString("prenom"));
                    soldeDepenseBestClient.setText("Solde dépensé : " + rs.getString("solde_depenser") + " €");
                    nbPizzaCommanderBestClient.setText("Nombre pizza commandé : " + rs.getString("nb_pizza"));
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getBestClient : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void getLePlusNulLivreur() throws SQLException {
        String query = "SELECT l.nom, l.prenom, l.immatriculation, COUNT(c.id_commande) as nb_commandes " +
                "FROM livreur l JOIN commande c ON l.id_livreur = c.id_livreur " +
                "WHERE c.date_end_livraison > c.date_start_livraison + INTERVAL 30 MINUTE GROUP BY l.id_livreur " +
                "LIMIT 1;";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    firstnameWorstLivreur.setText(rs.getString("nom"));
                    lastnameWorstLivreur.setText(rs.getString("prenom"));
                    nbWorstLivraison.setText("Nombre de livraison ratée : " + rs.getString("nb_commandes"));
                    vehiculeWorstLivraison.setText("Véhicule immatriculation : " + rs.getString("immatriculation"));
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getLePlusNulLivreur : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }


    }

    public void getBestPizza() throws SQLException {
        String query = "SELECT p.nom_pizza, SUM(pd.quantite) AS nb_vendue " +
                "FROM pizza AS p JOIN pizzavendu AS pv ON p.nom_pizza = pv.nom_pizza JOIN produitsvendus AS pd ON pv.id_produits_vendus = pd.id_produits_vendus " +
                "GROUP BY p.nom_pizza " +
                "ORDER BY nb_vendue DESC;  ";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    bestPizzaName.setText("Pizza la plus apprécié : " + rs.getString("nom_pizza"));
                    bestPizzaNbVendue.setText("Nombre de fois commandé : " + rs.getString("nb_vendue"));
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getBestPizza : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void getBestIngredient() throws SQLException {
        String query = "SELECT i.nom_ingredient " +
                "FROM ingredient AS i JOIN composer AS c ON i.nom_ingredient = c.nom_ingredient JOIN pizza AS p ON c.nom_pizza = p.nom_pizza JOIN pizzavendu AS pv ON p.nom_pizza = pv.nom_pizza JOIN produitsvendus AS pd ON pv.id_produits_vendus = pd.id_produits_vendus " +
                "GROUP BY i.nom_ingredient " +
                "ORDER BY SUM(pd.quantite) DESC " +
                "LIMIT 1";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    bestIngredientName.setText("Ingrédient le plus apprécié : " + rs.getString("nom_ingredient"));
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getBestIngredient : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private double nbCommandeMoyenne;

    public void getMoyCommandeClient() throws SQLException {
        String query = "SELECT " +
                "    ROUND(AVG(moyenne_commande), 2) AS round_moyenne_commande, " +
                "    ROUND(AVG(prix_moyen_total), 2) AS round_prix_moyen_total " +
                "FROM ( " +
                "    SELECT " +
                "        (SUM(nb_commande) / COUNT(id_user)) AS moyenne_commande, " +
                "        (SUM(prix_moyen) / COUNT(id_user)) AS prix_moyen_total " +
                "    FROM ( " +
                "        SELECT " +
                "            u.id_user AS id_user, " +
                "            u.prenom, " +
                "            COUNT(c.id_commande) AS nb_commande, " +
                "            (SUM(c.prix_total) / COUNT(c.id_commande)) AS prix_moyen " +
                "        FROM user AS u " +
                "        JOIN commande AS c ON u.id_user = c.id_client " +
                "        WHERE u.operateur != 1 " +
                "        GROUP BY u.id_user " +
                "    ) AS nb_commande_par_client " +
                ") AS moyenne_prix_totals;";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    nbCommandeMoyenne = rs.getDouble("round_moyenne_commande");
                    nbCommandeParClient.setText("Moyenne de commande par client : " + rs.getString("round_moyenne_commande"));
                    moyennePrixParClient.setText("Prix moyen total par client : " + rs.getString("round_prix_moyen_total") + " €");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getMoyCommandeClient : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void getAllBestClient() {
        String query = "SELECT * " +
                "FROM (" +
                " SELECT u.id_user AS id_user, u.prenom, u.nom, u.email, COUNT(c.id_commande) AS nb_commande, (SUM(c.prix_total) / COUNT(c.id_commande)) AS prix_moyen " +
                "        FROM user AS u " +
                "        JOIN commande AS c ON u.id_user = c.id_client " +
                "        WHERE u.operateur != 1 " +
                "        GROUP BY u.id_user " +
                "ORDER BY nb_commande " +
                ") AS nb_commande_alluser " +
                "WHERE nb_commande_alluser.nb_commande >= ?";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setDouble(1, nbCommandeMoyenne);
            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    Client c = new Client(
                            rs.getInt("id_user"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            null,
                            null,
                            0.0,
                            false,
                            rs.getInt("nb_commande")
                    );
                    data.add(c);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getAllBestClient : " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void fillClientGridPane() {
        int row = 1;
        for (Client client : data) {
            addClientCell(client.getNom(), 0, row);
            addClientCell(client.getPrenom(), 1, row);
            addClientCell(client.getEmail(), 2, row);
            addClientCell(String.valueOf(client.getNbCommande()), 3, row);
            row++;
        }
    }

    private void addClientCell(String text, int col, int row) {
        Pane pane = new Pane();
        Label label = new Label(text);
        label.setStyle("-fx-padding: 5;");
        pane.getChildren().add(label);
        pane.setStyle("-fx-border-color: #4a6cf3; -fx-border-width: 1;");
        clientGridPane.add(pane, col, row);
    }

    private void getCA() {
        String query = "SELECT SUM(prix_total) AS CA FROM commande;";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    labelCA.setText("Chiffre d'affaire : " + rs.getString("CA") + " €");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getCA : " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() throws SQLException {
        // Ajouter des gestionnaires d'événements pour le bouton
        deconnexionButton.setOnMousePressed(event -> increaseButtonSize());
        deconnexionButton.setOnMouseReleased(event -> resetButtonSize());

        // Ajuster la largeur des HBox
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue(); // Nouvelle largeur de la fenêtre
            clientInfoBox.setPrefWidth(newWidth * 0.45); // Ajuster la largeur de la boîte d'informations client
            livreurInfoBox.setPrefWidth(newWidth * 0.45); // Ajuster la largeur de la boîte d'informations livreur
            bestPizzaIngredientBox.setPrefWidth(newWidth * 0.60); // Ajuster la largeur de la boîte d'informations de la meilleure pizza
            statCommandeInfoBox.setPrefWidth(newWidth * 0.30); // Ajuster la largeur de la boîte d'informations sur les statistiques de commande
            clientGridPane.maxWidthProperty().bind(vboxClientGridPane.widthProperty().multiply(0.6));
            clientGridPane.prefWidthProperty().bind(vboxClientGridPane.widthProperty().multiply(0.6));
        });

        nameUser.setText(user.toString());

        getCA();
        getBestClient();
        getLePlusNulLivreur();
        getBestPizza();
        getBestIngredient();
        getMoyCommandeClient();
        getAllBestClient();
        fillClientGridPane();
    }

    // Méthode pour augmenter la taille du bouton
    private void increaseButtonSize() {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), deconnexionButton);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.1);
        scaleUp.play();
    }

    // Méthode pour réinitialiser la taille du bouton
    private void resetButtonSize() {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), deconnexionButton);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.play();
    }

}
