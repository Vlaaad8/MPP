package org.example.clientfx.DTO;

import java.io.Serializable;
public class TicketDTO implements Serializable {
    private int id;
    private FlightDTO flight;
    private int numberOfTickets;
    private String buyers;

    public TicketDTO(int id,FlightDTO flightID,int numberOfTickets,String buyers) {
        this.id=id;
        this.flight=flightID;
        this.numberOfTickets=numberOfTickets;
        this.buyers=buyers;
    }

    public int getId() {
        return id;
    }


    public FlightDTO getFlight() {
        return flight;
    }



    public int getNumberOfTickets() {
        return numberOfTickets;
    }


    public String getBuyers() {
        return buyers;
    }

    @Override
    public String toString() {
        return "TicketDTO{" +
                "id=" + id +
                ", flight=" + flight +
                ", numberOfTickets=" + numberOfTickets +
                ", buyers='" + buyers + '\'' +
                '}';
    }
}
