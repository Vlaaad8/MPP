package org.example.clientfx.DTO;

import org.example.clientfx.Employee;
import org.example.clientfx.Flight;
import org.example.clientfx.Ticket;

import java.time.LocalDateTime;

public class DTOUtils {
    public static Employee getFromDTO(EmployeeDTO dto) {
        int id = dto.getId();
        String user = dto.getUser();
        String password = dto.getPassword();
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        Employee employee = new Employee(user, password, firstName, lastName);
        employee.setId(id);
        return employee;
    }
    public static EmployeeDTO getDTO(Employee employee) {
        int id = employee.getId();
        String user = employee.getUser();
        String password = employee.getPassword();
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        EmployeeDTO employeeDTO = new EmployeeDTO(id,user, password, firstName, lastName);
        return employeeDTO;
    }

    public static Flight getFromDTO(FlightDTO dto) {
        int id = dto.getId();
        String origin = dto.getOrigin();
        String departure = dto.getDeparture();
        String airport = dto.getAirport();
        LocalDateTime dayTime = dto.getDayTime();
        int availableSeats = dto.getAvailableSeats();
        Flight flight = new Flight(origin, departure, availableSeats, airport, dayTime);
        flight.setId(id);
        return flight;
    }
    public static FlightDTO getDTO(Flight dto) {
        int id = dto.getId();
        String origin = dto.getOrigin();
        String departure = dto.getDeparture();
        String airport = dto.getAirport();
        LocalDateTime dayTime = dto.getDayTime();
        int availableSeats = dto.getAvailableSeats();
        FlightDTO flight = new FlightDTO(id,origin, departure, dayTime,availableSeats, airport);
        return flight;
    }

    public static Ticket getFromDTO(TicketDTO dto) {
        int id = dto.getId();
        Flight flight = getFromDTO(dto.getFlight());
        int numberOfTickets = dto.getNumberOfTickets();
        String buyers = dto.getBuyers();
        Ticket ticket = new Ticket(buyers, flight, numberOfTickets);
        ticket.setId(id);
        return ticket;
    }

    public static TicketDTO getDTO(Ticket ticket) {
        int id = ticket.getId();
        Flight flight = ticket.getFlight();
        int numberOfTickets = ticket.getNumberOfTickets();
        String buyers=ticket.getBuyer();
        TicketDTO dto=new TicketDTO(id,DTOUtils.getDTO(flight),numberOfTickets,buyers);
        return dto;
    }
}
