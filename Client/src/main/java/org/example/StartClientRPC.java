package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.clientfx.IServices;
import org.example.clientfx.ProxyRPC;

import java.io.IOException;
import java.util.Properties;

public class StartClientRPC extends Application {
    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(StartClientRPC.class.getResourceAsStream("/client.properties"));
            properties.list(System.out);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String serverIP = properties.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(properties.getProperty("server.port"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client running on"+" " + serverIP + ":" + serverPort);
        IServices server = new ProxyRPC(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("logView.fxml"));
        Parent root=loader.load();
        LogView logView = loader.<LogView>getController();
        logView.setService(server);

        FXMLLoader cloader = new FXMLLoader(getClass().getClassLoader().getResource("mainView.fxml"));
        Parent croot=cloader.load();
        MainView mainMenuView = cloader.<MainView>getController();
        mainMenuView.setService(server);

        logView.setMainMenuView(mainMenuView);
        logView.setParent(croot);

        stage.setTitle("Login");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
