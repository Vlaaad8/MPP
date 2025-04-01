package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.clientfx.Flight;
import org.example.clientfx.IServices;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainView {
    public DatePicker dateBox;
    public ComboBox<String> originBox;
    public ComboBox<String> departureBox;
    public Button searchButton;
    public TableView<Flight> mainTable;
    public TableColumn<Flight, String> originColumn;
    public TableColumn<Flight, String> departureColumn;
    public TableColumn<Flight, String> timeColumn;
    public TableColumn<Flight, String> airportColumn;
    public TableColumn<Flight, Integer> seatsColumn;
    public ObservableList<Flight> model = FXCollections.observableArrayList();
    public Button logOutButton;
    private Stage stage;
    private Stage stage1;
    private IServices service;


    public void setService(IServices service, Stage stage) {
        this.stage = stage;
        this.service = service;
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
        stage1.setTitle("Yahoo Messenger");
        BuyMenu mainMenuView = loader.getController();
        mainMenuView.setService(service,date,origin,departure);
        stage1.setHeight(472);
        stage1.setWidth(600);
        stage1.show();
    }

    public void handleLogOut(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mppjavafx/logView.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Yahoo Messenger");
        LogView mainMenuView = loader.getController();
        mainMenuView.setService(stage,service);
        stage.setHeight(472);
        stage.setWidth(600);
        stage.show();
        //stage1.close();
    }
}
