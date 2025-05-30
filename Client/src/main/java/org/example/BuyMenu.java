package org.example;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.clientfx.Flight;

import org.example.clientfx.Ticket;
import org.example.clientfx.grpc.BookingServiceGrpc;
import org.example.clientfx.grpc.NotificationServiceGrpc;
import org.example.clientfx.grpc.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class BuyMenu {
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
    @FXML
    public Button buyButton;
    @FXML
    public Spinner<Integer> numberBox;
    @FXML
    public TextField buyerText;
    ObservableList<Flight> model = FXCollections.observableArrayList();

    private Date date;
    private String origin;
    private String departure;
    private BookingServiceGrpc.BookingServiceBlockingStub service;
    private NotificationServiceGrpc.NotificationServiceStub observer;
    private Flight flight;

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
                        Service.FlightResponse flightResponse= service.searchFlight(ClientUtils.getDTO(flight));
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
    public void setData(Date date, String origin, String departure){
        this.date = date;
        this.origin = origin;
        this.departure = departure;
        initMain();
    }

    public void initialize() {
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        timeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDayTime(); // Directly get LocalDateTime
            String formattedTime = (dateTime != null) ? dateTime.format(formatter) : "";
            return new SimpleStringProperty(formattedTime);
        });
        airportColumn.setCellValueFactory(new PropertyValueFactory<>("airport"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        mainTable.setItems(model);
        numberBox.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

    }

    public void initMain() {
        flight = new Flight(origin, departure, 0, "", date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        flight.setId(0);
        Service.FlightResponse flightResponse= service.searchFlight(ClientUtils.getDTO(flight));
        List<Flight> flights=ClientUtils.getFlightList(flightResponse);
        model.setAll(flights);
    }


    public void handleBuy(ActionEvent actionEvent) {
        Flight flight2 = mainTable.getSelectionModel().getSelectedItem();
        int numberOfTickets = numberBox.getValue();
        String buyers = buyerText.getText();
        Ticket ticket = new Ticket(buyers, flight2, numberOfTickets);
        ticket.setId(0);
        service.addTicket(ClientUtils.getDTO(ticket));
        buyerText.clear();


    }
}