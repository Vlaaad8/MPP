package org.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.clientfx.*;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainView implements IObserver {
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

    private IServices service;
    private Employee employee;


    public void setService(IServices service) {
        this.service=service;
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
        Iterable<Flight> flights = service.getAllFlights();
        List<Flight> shownFlights = StreamSupport.stream(flights.spliterator(), false).collect(Collectors.toList());
        List<String> originSet = service.getOrigin().stream().toList();
        List<String> departureSet = service.getDestination().stream().toList();
        ObservableList<String> origins = FXCollections.observableArrayList(originSet);
        ObservableList<String> departures = FXCollections.observableArrayList(departureSet);
        originBox.setItems(origins);
        departureBox.setItems(departures);
        model.setAll(shownFlights);

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
        mainMenuView.setService(service);
        mainMenuView.setData(date, origin, departure);
        stage1.setHeight(472);
        stage1.setWidth(600);
        stage1.show();
    }

    public void handleLogOut(ActionEvent actionEvent) throws IOException {

    }

    @Override
    public void newTicketBought(Ticket ticket) throws Exception {
        Platform.runLater(() -> {
        List<Flight> flights = (List<Flight>) service.getAllFlights();
        ObservableList<Flight> observableFlights = FXCollections.observableArrayList(flights);
        model.setAll(observableFlights);

        });
    }
}
