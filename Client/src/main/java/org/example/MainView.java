package org.example;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.clientfx.*;
import org.example.clientfx.grpc.BookingServiceGrpc;
import org.example.clientfx.grpc.NotificationServiceGrpc;
import org.example.clientfx.grpc.Service;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


public class MainView{
    @FXML
    public DatePicker dateBox;
    @FXML
    public ComboBox<String> originBox;
    @FXML
    public ComboBox<String> departureBox;
    @FXML
    public Button searchButton;
    @FXML
    public TableView<Flight> mainTable;
    @FXML
    public TableColumn<Flight, String> originColumn;
    @FXML
    public TableColumn<Flight, String> departureColumn;
    @FXML
    public TableColumn<Flight, String> timeColumn;
    @FXML
    public TableColumn<Flight, String> airportColumn;
    @FXML
    public TableColumn<Flight, Integer> seatsColumn;
    public ObservableList<Flight> model = FXCollections.observableArrayList();
    @FXML
    public Button logOutButton;

    private Employee employee;

    private BookingServiceGrpc.BookingServiceBlockingStub service;
    private NotificationServiceGrpc.NotificationServiceStub observer;

    public void setStubs(BookingServiceGrpc.BookingServiceBlockingStub bookingStub,
                         NotificationServiceGrpc.NotificationServiceStub notificationStub) {
        this.service = bookingStub;
        this.observer = notificationStub;
        subscribeToNotifications();
    }
    private void subscribeToNotifications() {
        observer.newTicketBought(Empty.getDefaultInstance(), new StreamObserver<Service.Notification>() {
            @Override
            public void onNext(Service.Notification notification) {
                // A venit un eveniment ⇒ reîncarci UI-ul pe thread-ul JavaFX:
                Platform.runLater(() -> {
                    try {
                        System.out.println("Am primit o notificare!");
                        // Tu deja ai initMain(); poți să-l recall:
                        Service.FlightResponse flightResponse=service.getAllFlights(Empty.newBuilder().build());
                        List<Flight> flights=ClientUtils.getFlightList(flightResponse);
                        model.setAll(flights);
                        // Sau, dacă vrei să adaugi incremental:
                        // Flight f = ClientUtils.notificationToFlight(notification);
                        // model.add(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                // Dacă pică conexiunea, te poți reconecta aici:
                t.printStackTrace();
                // eventual: subscribeToNotifications();
            }

            @Override
            public void onCompleted() {
                // server-ul a închis stream-ul (rare)
            }
        });
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        mainTable.setItems(model);
        initMain();
    }

    public void initialize() {
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Convert Timestamp to a formatted String in the TableColumn
        timeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDayTime(); // Directly get LocalDateTime
            String formattedTime = (dateTime != null) ? dateTime.format(formatter) : "";
            return new SimpleStringProperty(formattedTime);
        });

        airportColumn.setCellValueFactory(new PropertyValueFactory<>("airport"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        mainTable.setItems(model);

    }

    public void initMain() {
        Service.FlightResponse flightResponse=service.getAllFlights(Empty.newBuilder().build());
        List<Flight> flights=ClientUtils.getFlightList(flightResponse);

        Service.CityResponse originResponse = service.getOrigin(Empty.newBuilder().build());
        List<String> originSet= ClientUtils.getCityList(originResponse);
        ObservableList<String> origins = FXCollections.observableArrayList(originSet);

        Service.CityResponse departureResponse = service.getDestination(Empty.newBuilder().build());
        List<String> departureSet= ClientUtils.getCityList(departureResponse);
        ObservableList<String> departures = FXCollections.observableArrayList(departureSet);

        originBox.setItems(origins);
        departureBox.setItems(departures);
        model.setAll(flights);

    }

    public void handleSearch(ActionEvent actionEvent) throws IOException {
        String origin=originBox.getValue();
        String departure=departureBox.getValue();
        LocalDate localDate= dateBox.getValue();
        Date date=Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/buyMenu.fxml"));
        AnchorPane root = loader.load();
        Stage stage1 = new Stage();
        Scene scene1 = new Scene(root);
        stage1.setScene(scene1);
        stage1.setTitle("Buy Menu");
        BuyMenu mainMenuView = loader.getController();
        mainMenuView.setStubs(service,observer);
        mainMenuView.setData(date, origin, departure);
        stage1.setHeight(472);
        stage1.setWidth(600);
        stage1.show();
    }

    public void handleLogOut(ActionEvent actionEvent) throws IOException {

    }

}
