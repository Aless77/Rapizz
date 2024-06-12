package com.example.rapizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Login extends Application {

    public User user;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Connection cnx = new DataBaseConnect().connect();

        LoginController controller = new LoginController(cnx);

        FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("login-view.fxml"));
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 400, 480);
        stage.setTitle("Rapizz Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
