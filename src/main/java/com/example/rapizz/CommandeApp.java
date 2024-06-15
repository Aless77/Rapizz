package com.example.rapizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CommandeApp extends Application {

    public User user;
    public Connection cnx;

    public static void startNewWindow(User user, Connection cnx) throws SQLException, IOException {
        CommandeApp app = new CommandeApp();
        app.user = user;
        app.cnx = cnx;
        app.start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            CommandeController controller = new CommandeController(user, cnx);
            FXMLLoader loader = new FXMLLoader();
            loader.setController(controller);
            loader.setLocation(CommandeApp.class.getResource("commande-view.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rapizz");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
