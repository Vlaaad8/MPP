package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.clientfx.Employee;
import org.example.clientfx.IObserver;
import org.example.clientfx.IServices;

import java.io.IOException;

public class LogView {
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField userField;
    @FXML
    public Button registerButton;
    @FXML
    public Button loginButton;
    public Label loginFailLabel;

    private IServices service;
    private Stage stage;

    public void setService(Stage stage, IServices service) {
        this.service = service;
        this.stage = stage;
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String username = userField.getText();
        String password = passwordField.getText();
        userField.clear();
        passwordField.clear();
        
        try {
            Employee employee = new Employee(username, password, "", "");
            employee.setId(0);
            Employee loggedInEmployee = this.service.login(employee);
            System.out.println("LogView-Client: Logged in employee: " + loggedInEmployee.getId());
            if (loggedInEmployee != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
                AnchorPane root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Yahoo Messenger");
                MainView mainMenuView = loader.getController();
                mainMenuView.setService(service, stage);
                stage.setHeight(472);
                stage.setWidth(600);
                stage.show();
            } else {
                loginFailLabel.setVisible(true);
                loginFailLabel.setText("Login failed! Invalid username or password.");
            }
        } catch (Exception e) {
            loginFailLabel.setVisible(true);
            loginFailLabel.setText("Login failed! " + e.getMessage());
        }
    }

}
