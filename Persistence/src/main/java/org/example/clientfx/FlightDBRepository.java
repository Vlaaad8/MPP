package org.example.clientfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clientfx.utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.*;


public class FlightDBRepository implements FlightRepository {
    private static JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public FlightDBRepository(Properties properties) {
        dbUtils = new JdbcUtils(properties);
    }

    @Override
    public List<Flight> findByAvailableSeats() {
        logger.traceEntry("Finding all the flights with available seats");
        List<Flight> flights = new ArrayList<>();

        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("select * from flights where availableSeats> 0")){
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String origin = resultSet.getString("origin");
                    String departure = resultSet.getString("departure");
                    int availableSeats = resultSet.getInt("availableSeats");
                    String airport = resultSet.getString("airport");
                    LocalDateTime daytime = resultSet.getTimestamp("dayTime").toLocalDateTime();
                    Flight flight = new Flight(origin, departure, availableSeats, airport, daytime);
                    flight.setId(id);
                    flights.add(flight);
                }
            }

        }catch(SQLException e){
            logger.error(e);
            System.err.println(e.getMessage());
        }
        logger.traceExit(flights);
        return flights;
    }

    @Override
    public List<Flight> findByDestination(String origin, String departure, Date departureDate) {
        logger.traceEntry("Searching for a flight from {} to {}, at the date {}",origin,departure,departureDate);
        List<Flight> flights=new ArrayList<>();
        Connection connection=dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("select * from flights where origin=? and departure=? and DATE(dayTime)=? and availableSeats>0")){
            preparedStatement.setString(1,origin);
            preparedStatement.setString(2,departure);
            preparedStatement.setDate(3,new java.sql.Date(departureDate.getTime()));
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    int availableSeats = resultSet.getInt("availableSeats");
                    String airport = resultSet.getString("airport");
                    LocalDateTime daytime = resultSet.getTimestamp("dayTime").toLocalDateTime();
                    Flight flight = new Flight(origin, departure, availableSeats, airport, daytime);
                    flight.setId(id);
                    flights.add(flight);
                }
            }
        }
        catch(SQLException e){
            System.err.println(e);
            logger.error(e);
        }
        return flights;
    }

    @Override
    public Optional<Flight> add(Flight entity) {
        logger.traceEntry("Adding a new flight {}", entity.toString());
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into flights(origin,departure,availableSeats,airport,dayTime) values(?,?,?,?,?)")) {
            preparedStatement.setString(1, entity.getOrigin());
            preparedStatement.setString(2, entity.getDeparture());
            preparedStatement.setInt(3, entity.getAvailableSeats());
            preparedStatement.setString(4, entity.getAirport());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(entity.getDayTime()));

            int resultSet = preparedStatement.executeUpdate();
            if (resultSet > 0) {
                logger.traceExit(entity.toString());
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Flight> delete(Flight entity) {
        logger.traceEntry("Deleting a flight {}", entity.toString());
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from flights where id=?")) {
            preparedStatement.setInt(1, entity.getId());
            int resultSet = preparedStatement.executeUpdate();
            if (resultSet > 0) {
                logger.traceExit(entity.toString());
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Flight> update(Integer integer, Flight entity) {
        Connection connection=dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("update flights set availableSeats= ? where id =?")){
            preparedStatement.setInt(1,entity.getAvailableSeats());
            preparedStatement.setInt(2,integer);
            int result = preparedStatement.executeUpdate();
            if(result > 0){
                return Optional.of(entity);
            }
        }
        catch (SQLException e){
            System.err.println(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Flight> findById(Integer integer) {
        logger.traceEntry("Finding flight by id {}", integer);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from flights where id=?")) {
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.first()) {
                    String origin = resultSet.getString("origin");
                    String departure = resultSet.getString("departure");
                    int availableSeats = resultSet.getInt("availableSeats");
                    String airport = resultSet.getString("airport");
                    LocalDateTime daytime = resultSet.getTimestamp("dayTime").toLocalDateTime();
                    Flight flight = new Flight(origin, departure, availableSeats, airport, daytime);
                    flight.setId(integer);
                    logger.traceExit(flight);
                    return Optional.of(flight);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<Flight> findAll() {
        logger.traceEntry("Finding all flights");
        List<Flight> flights = new ArrayList<>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from flights")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String origin = resultSet.getString("origin");
                    String departure = resultSet.getString("departure");
                    int availableSeats = resultSet.getInt("availableSeats");
                    String airport = resultSet.getString("airport");
                    LocalDateTime daytime = resultSet.getTimestamp("dayTime").toLocalDateTime();
                    Flight flight = new Flight(origin, departure, availableSeats, airport, daytime);
                    flight.setId(id);
                    flights.add(flight);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit(flights);
        return flights;
    }

    @Override
    public Collection<Flight> getAll() {
        return List.of();
    }

    @Override
    public Set<String> getOrigins(){
        Connection connection = dbUtils.getConnection();
        Set<String> origins = new HashSet<>();
        try(PreparedStatement preparedStatement=connection.prepareStatement("select distinct flights.origin from flights")){
            try(ResultSet resultSet= preparedStatement.executeQuery()){
                while(resultSet.next()){
                    String origin = resultSet.getString("origin");
                    origins.add(origin);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println(e);
        }
        return origins;
    }

@Override
public Set<String> getDepartures(){
    Connection connection = dbUtils.getConnection();
    Set<String> origins = new HashSet<>();
    try(PreparedStatement preparedStatement=connection.prepareStatement("select distinct departure from flights")){
        try(ResultSet resultSet= preparedStatement.executeQuery()){
            while(resultSet.next()){
                String origin = resultSet.getString("departure");
                origins.add(origin);
            }
        }
    }catch (SQLException e){
        logger.error(e);
        System.err.println(e);
    }
    return origins;
}
}

