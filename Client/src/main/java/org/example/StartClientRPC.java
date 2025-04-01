package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/logView.fxml"));
        AnchorPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Yahoo Messenger");
        LogView mainMenuView = loader.getController();
        mainMenuView.setService(stage, server);
        stage.setHeight(472);
        stage.setWidth(600);
        stage.show();
    }
}
