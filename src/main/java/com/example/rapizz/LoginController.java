package com.example.rapizz;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Main mainApp;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        // Add login logic here
        System.out.println("Login attempted by " + username);
    }

    @FXML
    protected void onSignUpButtonClick() {
        // Add sign-up logic here
        System.out.println("User wants to register");
    }

    public void setMainApplication(Main mainApp) {
        this.mainApp = mainApp;
    }
}
