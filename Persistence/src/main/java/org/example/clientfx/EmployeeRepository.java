package org.example.clientfx;


import java.util.Optional;

public interface EmployeeRepository extends Repository<Integer, Employee> {
    Optional<Employee> login(String user, String password);
}
