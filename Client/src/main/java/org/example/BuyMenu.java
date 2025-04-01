package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.clientfx.Flight;
import org.example.clientfx.IServices;
import org.example.clientfx.Ticket;

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
    public TableColumn<Flight, Integer> seatsColumn;
    public Button buyButton;
    public Spinner<Integer> numberBox;
    public TextField buyerText;
    ObservableList<Flight> model = FXCollections.observableArrayList();

    private Date date;
    private String origin;
    private String departure;
    private IServices service;

    public void setService(IServices service, Date date, String origin, String departure) {
        this.service = service;
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
        List<Flight> flights = service.searchFlight(flight);
        model.setAll(flights);
    }


    public void handleBuy(ActionEvent actionEvent) {
        Flight flight = mainTable.getSelectionModel().getSelectedItem();
        int numberOfTickets = numberBox.getValue();
        String buyers = buyerText.getText();
        Ticket ticket = new Ticket(buyers, flight, numberOfTickets);
        ticket.setId(0);
        service.addTicket(ticket);
        initMain();

    }
}