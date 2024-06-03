package com.example.rapizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainStatistique extends Application {

    public User user;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Connection cnx = new DataBaseConnect().connect();

        try {
            this.user = User.getUserById(1, cnx);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StatistiqueController controller = new StatistiqueController(user, cnx);

        FXMLLoader fxmlLoader = new FXMLLoader(MainStatistique.class.getResource("mainStatistique-view.fxml"));
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
