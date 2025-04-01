package org.example.clientfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clientfx.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TicketDBRepository implements TicketRepository {
    private static final Logger logger = LogManager.getLogger();
    private static JdbcUtils dbUtils;
    private FlightRepository flightRepository;

    public TicketDBRepository(Properties properties, FlightRepository flightRepository) {
        dbUtils = new JdbcUtils(properties);
        this.flightRepository = flightRepository;
    }

    @Override
    public Optional<Ticket> add(Ticket entity) {
        logger.traceEntry("Saving the ticket {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into tickets(flightId, numberOfTickets, buyers) values(?,?,?)")) {
            preparedStatement.setInt(1, entity.getFlight().getId());
            preparedStatement.setInt(2, entity.getNumberOfTickets());
            preparedStatement.setString(3, entity.getBuyer());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.traceExit();
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> delete(Ticket entity) {
        logger.traceEntry("Deleting the ticket {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from tickets where id= ?")) {
            preparedStatement.setInt(1, entity.getId());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.traceExit("Delete successful");
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> update(Integer integer, Ticket entity) {
        Connection connection = dbUtils.getConnection();
        logger.traceEntry("Updating the ticket {}", entity);


        try (PreparedStatement preparedStatement = connection.prepareStatement("update tickets set flightId=?,buyers=?,numberOfTickets=? where id = ?")) {
            preparedStatement.setInt(1, entity.getFlight().getId());
            preparedStatement.setString(2, entity.getBuyer());
            preparedStatement.setInt(3, entity.getNumberOfTickets());
            preparedStatement.setInt(4, integer);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.traceExit("Update successful");
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> findById(Integer integer) {
        logger.traceEntry("Finding the ticket by id {}", integer);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from tickets where id = ?")) {
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int flightId = resultSet.getInt("flightId");
                Flight flight = flightRepository.findById(flightId).orElse(null);
                String buyers = resultSet.getString("buyers");
                int numberOfTickets = resultSet.getInt("numberOfTickets");
                Ticket ticket = new Ticket(buyers, flight, numberOfTickets);
                ticket.setId(integer);
                logger.traceExit("Find successful");
                return Optional.of(ticket);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit("Find successful");
        return Optional.empty();
    }

    @Override
    public Iterable<Ticket> findAll() {
        logger.traceEntry("Finding all tickets");
        List<Ticket> tickets = new ArrayList<>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from tickets")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int flightId = resultSet.getInt("flightId");
                    Flight flight = flightRepository.findById(flightId).orElse(null);
                    String buyers = resultSet.getString("buyers");
                    int numberOfTickets = resultSet.getInt("numberOfTickets");
                    Ticket ticket = new Ticket(buyers, flight, numberOfTickets);
                    ticket.setId(id);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit("Find successful");
        return tickets;
    }


    @Override
    public Collection<Ticket> getAll() {
        return List.of();
    }
}
