package com.example.rapizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Connection cnx = new DataBaseConnect().connect();

        // requete
        /*String query = "SELECT * FROM `these_soutenance_these`";
        try {
            // Création de l'objet Statement
            Statement stmt = cnx.createStatement();
            // Exécution de la requête
            ResultSet rs = stmt.executeQuery(query);
            // Récupération des données
            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("id_these") + " | Sujet : " + rs.getString("sujet") +"\n");
            }
        } catch (SQLException e) {
            System.out.println("Une erreur s'est produite lors de la connexion à la base de données. "+e.getMessage());
        }*/
    }

    public static void main(String[] args) {
        launch();
    }
}