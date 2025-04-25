package org.example;

import org.example.clientfx.Flight;
import org.example.clientfx.Ticket;
import org.example.clientfx.grpc.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ClientUtils {
    public static List<Flight> getFlightList(Service.FlightResponse flightResponse) {
        List<Flight> flights = new ArrayList<>();
        for (Service.FlightDTO flightDTO : flightResponse.getFlight().getFlightsList()) {
            Flight flight = recoverFlight(flightDTO);
            flights.add(flight);
        }
        return flights;
    }
    public static List<String> getCityList(Service.CityResponse cityResponse) {
        List<String> cityList = new ArrayList<>();
        cityResponse.getCity().getCitiesList().forEach(city -> {cityList.add(city);});
        return cityList;
    }
    public static Flight recoverFlight(Service.FlightDTO flightDTO) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm:ss a").withLocale(java.util.Locale.ENGLISH);

        LocalDateTime date=LocalDateTime.now();
        try {
            date = LocalDateTime.parse(flightDTO.getDate().toUpperCase(Locale.ENGLISH), fmt);
        }
        catch (DateTimeParseException e) {
            System.out.println("Format invalid pentru "+flightDTO.getDate());
        }
        Flight tempFlight = new Flight(flightDTO.getOrigin(), flightDTO.getDestination(), flightDTO.getAvailableSeats(), flightDTO.getAirport(), date);
        tempFlight.setId(flightDTO.getId());
        return tempFlight;
    }
    public static Service.FlightDTO getDTO(Flight flight) {

        Service.FlightDTO flightDTO = Service.FlightDTO.newBuilder()
                .setId(flight.getId())
                .setOrigin(flight.getOrigin())
                .setDestination(flight.getDeparture())
                .setAirport(flight.getAirport())
                .setAvailableSeats(flight.getAvailableSeats())
                .setDate(flight.getDayTime().toString()).build();
        return flightDTO;
        }

    public static Service.TicketDTO getDTO(Ticket ticket) {
        Service.FlightDTO flightDTO = getDTO(ticket.getFlight());
        Service.TicketDTO ticketDTO= Service.TicketDTO.newBuilder()
                .setId(ticket.getId())
                .setFlight(flightDTO)
                .setNumberOfTickets(ticket.getNumberOfTickets())
                .setBuyers(ticket.getBuyer()).build();
        return ticketDTO;
    }
}


