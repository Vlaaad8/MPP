package org.example.clientfx;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServicesImplementation implements IServices {
    private EmployeeRepository employeeRepository;
    private FlightRepository flightRepository;
    private TicketRepository ticketRepository;
    private Map<Integer, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public ServicesImplementation(EmployeeRepository employeeRepository, FlightRepository flightRepository, TicketRepository ticketRepository) {
        this.employeeRepository = employeeRepository;
        this.flightRepository = flightRepository;
        this.ticketRepository = ticketRepository;
    }
    @Override
    public void addTicket(Ticket ticket) {
        System.out.println("Serverul a intrat in metoda addTicket");
        Ticket ticket1 = ticketRepository.add(ticket).orElse(null);
        if (ticket1 != null) {
            System.out.println("Biletul a fost adaugat cu succes");
        } else {
            System.out.println("Biletul nu a fost adaugat");
        }
        Flight flight=ticket.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats()-ticket.getNumberOfTickets());
        flightRepository.update(flight.getId(), flight);
    }

    @Override
    public Iterable<Flight> getAllFlights() {
        System.out.println("Serverul a intrat in metoda getAllFlights");
        Iterable<Flight> flights = flightRepository.findAll();
        return flights;

    }

    @Override
    public Employee login(Employee employee) {
        System.out.println("Serverul a intrat in metoda Login");
        Employee foundEmployee=employeeRepository.login(employee.getUser(),employee.getPassword()).orElse(null);
        if(foundEmployee!=null){
            System.out.println(foundEmployee.getId()+" "+foundEmployee.getFirstName()+" "+foundEmployee.getPassword());
//            if (this.loggedClients.get(employee.getId()) != null) {
//                System.out.println("Eroare");
//            } else {
                //this.loggedClients.put(employee.getId(),observer);
            System.out.println("Am gasit angajatul "+foundEmployee.getId()+" "+foundEmployee.getUser()+" "+foundEmployee.getPassword());
                return foundEmployee;
//            }
        }
        return null;
    }

    @Override
    public Set<String> getOrigin() {
        System.out.println("Serverul a intrat in metoda getOrigin");
        Set<String> origin = flightRepository.getOrigins();
        return origin;
    }

    @Override
    public Set<String> getDestination() {
        System.out.println("Serverul a intrat in metoda getDestination");
        Set<String> destination = flightRepository.getDepartures();
        return destination;
    }

    @Override
    public List<Flight> searchFlight(Flight flight) {
        System.out.println("Serverul a intrat in metoda searchFlight");
        LocalDateTime localDateTime=flight.getDayTime();
        List<Flight> flights = flightRepository.findByDestination(flight.getOrigin(), flight.getDeparture(), Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return flights;
    }
}
