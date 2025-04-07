package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.clientfx.Employee;
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
    private MainView mainMenuView;
    private Parent mainChatParent;

    private IServices service;

    public void setService(IServices service) {
        this.service = service;
    }

    public void setMainMenuView(MainView mainMenuView) {
        this.mainMenuView = mainMenuView;
    }

    public void setParent(Parent p) {
        mainChatParent = p;
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String username = userField.getText();
        String password = passwordField.getText();
        userField.clear();
        passwordField.clear();

        try {
            Employee employee = new Employee(username, password, "", "");
            employee.setId(0);


            Employee loggedInEmployee = this.service.login(employee, mainMenuView);
            System.out.println("LogView-Client: Logged in employee: " + loggedInEmployee.getId());
            if (loggedInEmployee.getId() != null) {
                Stage stage = new Stage();
                stage.setTitle("Chat Window");
                stage.setScene(new Scene(mainChatParent));
                mainMenuView.setEmployee(loggedInEmployee);
                System.out.println(mainChatParent.toString());
                stage.show();
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();


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
