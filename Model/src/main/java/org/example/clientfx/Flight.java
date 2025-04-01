package org.example.clientfx;

import java.time.LocalDateTime;

public class Flight extends Entity<Integer> {
    private String origin;
    private String departure;
    private int availableSeats;
    private String airport;
    private LocalDateTime daytime;

    public Flight(String origin, String departure, int availableSeats, String airport, LocalDateTime daytime) {
        this.origin = origin;
        this.departure = departure;
        this.availableSeats = availableSeats;
        this.airport = airport;
        this.daytime = daytime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public LocalDateTime getDayTime() {
        return daytime;
    }

    public void setDaytime(LocalDateTime daytime) {
        this.daytime = daytime;
    }

    @Override
    public String toString() {
        return "Flight:" + origin + ' ' +
                 departure +  ' ' +
                 availableSeats + ' '+
                 airport + ' ' +
                 daytime + '\'';
    }
}
