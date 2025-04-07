package org.example.clientfx;

import org.example.clientfx.DTO.DTOUtils;
import org.example.clientfx.DTO.EmployeeDTO;
import org.example.clientfx.DTO.FlightDTO;
import org.example.clientfx.DTO.TicketDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyRPC implements IServices {
    private String host;
    private int port;
    private IObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private BlockingQueue<Response> qResponses;
    private volatile boolean finished;

    public ProxyRPC(String host, int port) {
        this.host = host;
        this.port = port;
        qResponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() throws IOException {
        try {
            socket = new Socket(this.host, this.port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            finished = false;
            startReader();
            System.out.println("Connection initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize connection: " + e.getMessage());
            throw new IOException("Failed to initialize connection: " + e.getMessage());
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.NEW_TICKET) {
            TicketDTO ticketDTO = (TicketDTO) response.data();
            Ticket ticket = DTOUtils.getFromDTO(ticketDTO);
            try {
                this.client.newTicketBought(ticket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void addTicket(Ticket ticket) {
        TicketDTO ticketDTO = DTOUtils.getDTO(ticket);
        Request request = new Request.Builder()
                .type(RequestType.ADD_TICKET)
                .data(ticketDTO)
                .build();
        sendRequest(request);
        try {
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                throw new RuntimeException(response.data().toString());
            }
            if(response.type() == ResponseType.OK) {
                System.out.println("Ticket added successfully");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to add ticket: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Flight> getAllFlights() {
        Request request = new Request.Builder()
                .type(RequestType.GET_FLIGHTS)
                .build();
        sendRequest(request);
        try {
            Response response = readResponse();
            if (response.type() == ResponseType.RECEIVE_FLIGHTS) {
                List<Flight> normalizedFlights= new ArrayList<>();
                for (FlightDTO flightDTO : (List<FlightDTO>) response.data()) {
                    Flight flight = DTOUtils.getFromDTO(flightDTO);
                    normalizedFlights.add(flight);
                }
                return normalizedFlights;

            } else {
                throw new RuntimeException(response.data().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get flights: " + e.getMessage());
        }
    }

    @Override
    public Employee login(Employee employee,IObserver client) {
        try {
            this.initializeConnection();
            EmployeeDTO employeeDTO = DTOUtils.getDTO(employee);
            System.out.println("Am intrat in login");
            Request request = new Request.Builder()
                    .type(RequestType.LOGIN)
                    .data(employeeDTO)
                    .build();
            this.sendRequest(request);
            System.out.println("Am trimis requestul de login");
            Response response = this.readResponse();
            if (response.type() == ResponseType.OK) {
                this.client= client;
                return employee;
            }
            if (response.type() == ResponseType.ERROR) {
                closeConnection();
                System.out.println("Erroare la logare");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Set<String> getOrigin() {
        Request request = new Request.Builder()
                .type(RequestType.GET_ORIGIN)
                .build();
        sendRequest(request);
        try {
            Response response = readResponse();
            if (response.type() == ResponseType.RECEIVE_ORIGIN) {
                return (Set<String>) response.data();
            } else {
                throw new RuntimeException(response.data().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get origins: " + e.getMessage());
        }
    }

    @Override
    public Set<String> getDestination() {
        Request request = new Request.Builder()
                .type(RequestType.GET_DESTINATION)
                .build();
        sendRequest(request);
        try {
            Response response = readResponse();
            if (response.type() == ResponseType.RECEIVE_DESTINATION) {
                return (Set<String>) response.data();
            } else {
                throw new RuntimeException(response.data().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get destinations: " + e.getMessage());
        }
    }

    @Override
    public List<Flight> searchFlight(Flight flight) {
        FlightDTO flightDTO1= DTOUtils.getDTO(flight);
        Request request = new Request.Builder()
                .type(RequestType.SEARCH)
                .data(flightDTO1)
                .build();
        sendRequest(request);
        try {
            Response response = readResponse();
            if (response.type() == ResponseType.RECEIVE_SEARCH) {
                List<Flight> normalizedFlights= new ArrayList<>();
                for (FlightDTO flightDTO : (List<FlightDTO>) response.data()) {
                    Flight flight2 = DTOUtils.getFromDTO(flightDTO);
                    normalizedFlights.add(flight2);
                }
                return normalizedFlights;
            } else {
                throw new RuntimeException(response.data().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to search flights: " + e.getMessage());
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.NEW_TICKET;
    }

    private void sendRequest(Request request) {
        try {
            System.out.println("Sending request: " + request.type());
            output.writeObject(request);
            output.flush();
            System.out.println("Request sent successfully");
        } catch (IOException e) {
            System.err.println("Failed to send request: " + e.getMessage());
            throw new RuntimeException("Failed to send request: " + e.getMessage());
        }
    }

    private Response readResponse() throws IOException {
        try {
            return this.qResponses.take();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void closeConnection() {
        this.finished = true;
        try {
            this.input.close();
            this.output.close();
            this.socket.close();
            this.client = null;
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received: " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qResponses.put((Response) response);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("PROXY: Error in reader thread: " + e.getMessage());
                    closeConnection();
                }
            }
        }
    }
}
