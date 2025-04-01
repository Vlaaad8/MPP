package org.example.clientfx.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FlightDTO implements Serializable {
    private int id;
    private String origin;
    private String departure;
    private LocalDateTime dayTime;
    private String airport;
    private int availableSeats;

    public FlightDTO(int id, String origin, String departure, LocalDateTime dayTime, int availableSeats, String airport) {
        this.id=id;
        this.origin=origin;
        this.departure=departure;
        this.dayTime=dayTime;
        this.availableSeats=availableSeats;
        this.airport=airport;
    }

    public int getId() {
        return id;
    }


    public String getOrigin() {
        return origin;
    }


    public String getDeparture() {
        return departure;
    }


    public LocalDateTime getDayTime() {
        return dayTime;
    }


    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getAirport() {
        return airport;
    }

    @Override
    public String toString() {
        return "FlightDTO{" +
                "id='" + id + '\'' +
                ", origin='" + origin + '\'' +
                ", departure='" + departure + '\'' +
                ", dayTime='" + dayTime + '\'' +
                ", availableSeats='" + availableSeats + '\'' +
                ",aiport='" + airport + '\'' +
                '}';
    }
}
