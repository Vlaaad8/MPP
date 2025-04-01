//package org.example.mppjavafx;
//
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//import org.example.LogView;
//import org.example.mppjavafx.service.Service;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Properties;
//
//public class Main extends Application {
//    public static void main(String[] args) {
//        launch();
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        Properties props=new Properties();
//        try {
//            props.load(new FileReader("bd.config"));
//        } catch (IOException e) {
//            System.out.println("Cannot find bd.config "+e);
//        }
//
//        EmployeeRepository employeeDBRepository = new EmployeeDBRepository(props);
//        FlightRepository flightRepository = new FlightDBRepository(props);
//        TicketRepository ticketRepository= new TicketDBRepository(props,flightRepository);
//
//        Service service=new Service(employeeDBRepository,flightRepository,ticketRepository);
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mppjavafx/logView.fxml"));
//        AnchorPane root = loader.load();
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle("WizzAir App");
//        LogView mainMenuView = loader.getController();
//        mainMenuView.setService(service,stage);
//        stage.setHeight(472);
//        stage.setWidth(600);
//        stage.show();
//    }
//}
