package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.clientfx.grpc.BookingServiceGrpc;
import org.example.clientfx.grpc.NotificationServiceGrpc;

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

        ManagedChannel channel=ManagedChannelBuilder.forAddress(serverIP, serverPort).usePlaintext().build();
        System.out.println("Client running on"+" " + serverIP + ":" + serverPort);

        BookingServiceGrpc.BookingServiceBlockingStub bookingService = BookingServiceGrpc.newBlockingStub(channel);
        NotificationServiceGrpc.NotificationServiceStub notificationService = NotificationServiceGrpc.newStub(channel);



        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("logView.fxml"));
        Parent root=loader.load();
        LogView logView = loader.<LogView>getController();
        logView.setStubs(bookingService,notificationService);

        FXMLLoader cloader = new FXMLLoader(getClass().getClassLoader().getResource("mainView.fxml"));
        Parent croot=cloader.load();
        MainView mainMenuView = cloader.<MainView>getController();
        mainMenuView.setStubs(bookingService,notificationService);

        logView.setMainMenuView(mainMenuView);
        logView.setParent(croot);

        stage.setTitle("Login");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
