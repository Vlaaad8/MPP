package org.example.clientfx;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface FlightRepository extends Repository<Integer, Flight> {
    List<Flight> findByAvailableSeats();

    List<Flight> findByDestination(String origin, String departure, Date departureDate);

    Set<String> getOrigins();
    Set<String> getDepartures();

}
