package com.example.rapizz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        showLoginView();
    }

    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loader.load(), 320, 240);
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        LoginController controller = loader.getController();
        controller.setMainApplication(this);
    }

    public void showMainView() throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene mainScene = new Scene(mainLoader.load(), 800, 600);
        primaryStage.setTitle("Main Application");
        primaryStage.setScene(mainScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
