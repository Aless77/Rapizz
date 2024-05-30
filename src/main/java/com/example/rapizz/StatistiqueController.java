package com.example.rapizz;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

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

    @FXML
    public void initialize() {
        // Ajouter des gestionnaires d'événements pour le bouton
        deconnexionButton.setOnMousePressed(event -> increaseButtonSize());
        deconnexionButton.setOnMouseReleased(event -> resetButtonSize());

        // Ajuster la largeur des HBox
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            clientInfoBox.setPrefWidth(newWidth * 0.45);
            livreurInfoBox.setPrefWidth(newWidth * 0.45);
            bestPizzaIngredientBox.setPrefWidth(newWidth * 0.75);
            vehiculeNoUseInfoBox.setPrefWidth(newWidth * 0.45);
            statCommandeInfoBox.setPrefWidth(newWidth * 0.45);
        });
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
