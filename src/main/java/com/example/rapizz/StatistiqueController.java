package com.example.rapizz;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
        });

        nameUser.setText(user.toString());

        getBestClient();


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
