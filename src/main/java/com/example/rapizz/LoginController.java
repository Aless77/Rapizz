package com.example.rapizz;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private Button loginButton;

    private Connection cnx;

    @FXML
    public void initialize() {
        // Ajouter un écouteur d'événement pour le clic sur le bouton
        loginButton.setOnMouseClicked(e -> {
            try {
                handleButtonClick();
            } catch (SQLException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public LoginController(Connection cnx) {
        this.cnx = cnx;
    }

    // Méthode pour gérer le clic sur le bouton
    private void handleButtonClick() throws SQLException, IOException {

        String username = usernameField.getText();
        String password = passwordField.getText();

        // Augmenter la taille du bouton
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), loginButton);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.1);

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Veuillez remplir tous les champs.");
            return;
        }
        else {
            User user = User.getUser(username, password, cnx);
            if (user == null) {
                errorMessage.setText("Nom d'utilisateur ou mot de passe incorrect.");
                return;
            }

            if (user.isOperateur()) {
                // Ouvrir la fenêtre de statistiques
                MainStatistique.startNewWindow(user, cnx);
            } else {
                // Ouvrir la fenêtre de commande
                //MainCommande.startNewWindow(user, cnx);
                System.out.println("User is not an operator");
            }
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();
        }

        scaleUp.setOnFinished(event -> {
            // Réinitialiser la taille du bouton après un court délai
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), loginButton);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
        scaleUp.play();
    }
}
