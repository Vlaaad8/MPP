package org.example.clientfx;

public class Ticket extends Entity<Integer> {
    private String buyer;
    private Flight flight;
    private int numberOfTickets;

    public Ticket(String buyer, Flight flight, int numberOfTickets) {
        this.buyer = buyer;
        this.flight = flight;
        this.numberOfTickets = numberOfTickets;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "buyer='" + buyer + '\'' +
                ", flight=" + flight +
                ", numberOfTickets=" + numberOfTickets +
                '}';
    }
}
