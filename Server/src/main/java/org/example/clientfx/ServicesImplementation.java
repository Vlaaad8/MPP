package org.example.clientfx;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImplementation implements IServices {
    private final int defaultThreadsNo = 5;
    private EmployeeRepository employeeRepository;
    private FlightRepository flightRepository;
    private TicketRepository ticketRepository;
    private Map<String, IObserver> loggedClients;

    public ServicesImplementation(EmployeeRepository employeeRepository, FlightRepository flightRepository, TicketRepository ticketRepository) {
        this.employeeRepository = employeeRepository;
        this.flightRepository = flightRepository;
        this.ticketRepository = ticketRepository;
        loggedClients= new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void addTicket(Ticket ticket) {
        ticketRepository.add(ticket).orElse(null);
        Flight flight = ticket.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() - ticket.getNumberOfTickets());
        flightRepository.update(flight.getId(), flight);

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (IObserver obs : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    obs.newTicketBought(ticket);
                } catch (Exception e) {
                    System.out.println("Error notifying observer: " + e.getMessage());
                }
            });
        }
        System.out.println("Am notificat pe toti cu bine!");
        executor.shutdown();
    }

    @Override
    public Iterable<Flight> getAllFlights() {
        System.out.println("Serverul a intrat in metoda getAllFlights");
        Iterable<Flight> flights = flightRepository.findAll();
        return flights;

    }

    @Override
    public Employee login(Employee employee, IObserver client) {
        Employee foundEmployee = employeeRepository.login(employee.getUser(), employee.getPassword()).orElse(null);
        if (foundEmployee != null) {
            if (loggedClients.get(employee.getUser())!=null) {
                System.out.println("Clientul este deja logat");
            } else {
                this.loggedClients.put(employee.getUser(), client);
                System.out.println("Am gasit angajatul " + foundEmployee.getId() + " " + foundEmployee.getUser() + " " + foundEmployee.getPassword());

                return foundEmployee;
            }
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
        LocalDateTime localDateTime = flight.getDayTime();
        List<Flight> flights = flightRepository.findByDestination(flight.getOrigin(), flight.getDeparture(), Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return flights;
    }
}
