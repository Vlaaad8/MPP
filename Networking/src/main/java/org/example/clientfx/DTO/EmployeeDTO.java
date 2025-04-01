package org.example.clientfx.DTO;

import java.io.Serializable;

public class EmployeeDTO implements Serializable {
    private int id;
    private String user;
    private String password;
    private String firstName;
    private String lastName;

    public EmployeeDTO(int id, String user, String password, String firstName, String lastName) {
        this.id=id;
        this.user=user;
        this.password=password;
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public int getId() {
        return id;
    }


    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
