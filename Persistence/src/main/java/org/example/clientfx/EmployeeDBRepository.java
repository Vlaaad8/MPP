package org.example.clientfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clientfx.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EmployeeDBRepository implements EmployeeRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public EmployeeDBRepository(Properties properties) {
        dbUtils = new JdbcUtils(properties);
    }


    @Override
    public Optional<Employee> login(String user, String password) {
        logger.traceEntry("Finding employee by user and password {}, {}", user, password);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement ps = connection.prepareStatement("select * from employees where user = ? and password = ?")) {
            ps.setString(1, user);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    Employee employee = new Employee(user, password, firstName, lastName);
                    employee.setId(id);
                    return Optional.of(employee);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.err.println(exception.getMessage());
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Employee> add(Employee entity) {
        logger.traceEntry("Adding employee {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into employees(user,password,firstName,lastName) VALUES(?,?,?,?)")) {
            preparedStatement.setString(1, entity.getUser());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFirstName());
            preparedStatement.setString(4, entity.getLastName());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.traceExit(entity);
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
    public Optional<Employee> delete(Employee entity) {
        logger.traceEntry("Deleting employee {}", entity);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("delete from employees where id = ?")){
            preparedStatement.setInt(1,entity.getId());
            int result=preparedStatement.executeUpdate();
            if(result>0){
                logger.traceExit(entity);
                return Optional.of(entity);
            }
        }catch(SQLException e){
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Employee> update(Integer integer, Employee entity) {
        logger.traceEntry("Updating employee {}", entity);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("update employees set \"user\"=?,\"password\"=?,firstName=?,lastName=? where id=?")){
            preparedStatement.setString(1, entity.getUser());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFirstName());
            preparedStatement.setString(4, entity.getLastName());
            preparedStatement.setInt(5, integer);
            int result=preparedStatement.executeUpdate();
            if(result>0){
                logger.traceExit(entity);
                return Optional.of(entity);
            }
        }
        catch(SQLException e){
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
        }

    @Override
    public Optional<Employee> findById(Integer integer) {
        logger.traceEntry("Finding employee by id {}", integer);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("select * from employees where id=?")) {
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String user = resultSet.getString("user");
                    String password = resultSet.getString("password");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    Employee employee = new Employee(user, password, firstName, lastName);
                    employee.setId(integer);
                    logger.traceExit(employee.toString());
                    return Optional.of(employee);
                }
            }
        }catch(SQLException e){
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<Employee> findAll() {
        logger.traceEntry("Finding all employees");
        List<Employee> employees = new ArrayList<>();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=connection.prepareStatement("select * from employees")){
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int id= resultSet.getInt("id");
                    String user = resultSet.getString("user");
                    String password = resultSet.getString("password");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    Employee employee = new Employee(user, password, firstName, lastName);
                    employee.setId(id);
                    employees.add(employee);
                }
            }
        }catch(SQLException e){
            logger.error(e);
            System.err.println(e);
        }
        logger.traceExit(employees.toString());
        return employees;
    }

    @Override
    public Collection<Employee> getAll() {
        return List.of();
    }
}
