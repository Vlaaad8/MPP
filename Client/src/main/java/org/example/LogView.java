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

import org.example.clientfx.grpc.BookingServiceGrpc;
import org.example.clientfx.grpc.NotificationServiceGrpc;
import org.example.clientfx.grpc.Service;



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

    private BookingServiceGrpc.BookingServiceBlockingStub service;
    private NotificationServiceGrpc.NotificationServiceStub observer;

    public void setStubs(BookingServiceGrpc.BookingServiceBlockingStub bookingStub,
                         NotificationServiceGrpc.NotificationServiceStub notificationStub) {
        this.service = bookingStub;
        this.observer = notificationStub;
    }

    public void setMainMenuView(MainView mainMenuView) {
        this.mainMenuView = mainMenuView;
    }

    public void setParent(Parent p) {
        mainChatParent = p;
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String username = userField.getText();
        String passwordNoCrypt = passwordField.getText();
        userField.clear();
        passwordField.clear();

        try {
            Employee employee = new Employee(username, passwordNoCrypt, "", "");
            employee.setId(0);
            var a= Service.EmployeeDTO.newBuilder().setId(0).setUser(employee.getUser()).setPassword(employee.getPassword()).setLastName("").setFirstName("").build();
            Service.DefaultResponse loggedInEmployee = service.login(a);
            //System.out.println("LogView-Client: Logged in employee: " + loggedInEmployee.getId());
            if (loggedInEmployee.getSuccess()) {
                Stage stage = new Stage();
                stage.setTitle("Chat Window");
                stage.setScene(new Scene(mainChatParent));
                mainMenuView.setEmployee(employee);
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
