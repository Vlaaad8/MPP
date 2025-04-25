package org.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.clientfx.Flight;
import org.example.clientfx.IObserver;
import org.example.clientfx.IServices;
import org.example.clientfx.Ticket;
import org.example.clientfx.grpc.BookingServiceGrpc;
import org.example.clientfx.grpc.NotificationServiceGrpc;
import org.example.clientfx.grpc.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class BuyMenu implements IObserver {
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

    public void setStubs(BookingServiceGrpc.BookingServiceBlockingStub bookingStub,
                         NotificationServiceGrpc.NotificationServiceStub notificationStub) {
        this.service = bookingStub;
        this.observer = notificationStub;
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
        Flight flight = new Flight(origin, departure, 0, "", date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        flight.setId(0);
        Service.FlightResponse flightResponse= service.searchFlight(ClientUtils.getDTO(flight));
        List<Flight> flights=ClientUtils.getFlightList(flightResponse);
        model.setAll(flights);
    }


    public void handleBuy(ActionEvent actionEvent) {
        Flight flight = mainTable.getSelectionModel().getSelectedItem();
        int numberOfTickets = numberBox.getValue();
        String buyers = buyerText.getText();
        Ticket ticket = new Ticket(buyers, flight, numberOfTickets);
        ticket.setId(0);
        service.addTicket(ClientUtils.getDTO(ticket));
        buyerText.clear();


    }

    @Override
    public void newTicketBought(Ticket ticket) throws Exception {
//        Platform.runLater(() -> {
//            try {
//                initMain();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//        });
    }
}