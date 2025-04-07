package org.example.clientfx;

import java.util.List;
import java.util.Set;

public interface IServices {
    void addTicket(Ticket ticket);
    Iterable<Flight> getAllFlights();
    Employee login(Employee employee, IObserver client);
    Set<String> getOrigin();
    Set<String> getDestination();
    List<Flight> searchFlight(Flight flight);
}
