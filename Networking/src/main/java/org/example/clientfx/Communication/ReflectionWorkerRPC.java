package org.example.clientfx.Communication;

import org.example.clientfx.*;
import org.example.clientfx.DTO.DTOUtils;
import org.example.clientfx.DTO.EmployeeDTO;
import org.example.clientfx.DTO.FlightDTO;
import org.example.clientfx.DTO.TicketDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;


public class ReflectionWorkerRPC implements Runnable, IObserver {
    private IServices server;
    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private volatile boolean connected;

    public ReflectionWorkerRPC(IServices services, Socket client) {
        this.server = services;
        this.client = client;
        try {
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
            input = new ObjectInputStream(client.getInputStream());
            connected = true;
            System.out.println("Am fost creat la cererea clientului");
        } catch (IOException e) {
            System.out.println("error");
        }
    }
    private static final Response okResponse = (new Response.Builder()).type(ResponseType.OK).build();
    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                System.out.println("Request received: " + request);
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error in worker run: " + e.getMessage());
                connected = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Error in worker sleep: " + e.getMessage());
            }
        }
        try {
            input.close();
            output.close();
            client.close();
        } catch (IOException e) {
            System.err.println("Error closing worker connection: " + e.getMessage());
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + request.type();
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Handled request: " + request.type());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException{
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private Response handleLOGIN(Request request) {;
        EmployeeDTO employeeDTO = (EmployeeDTO) request.data();
        Employee employee = DTOUtils.getFromDTO(employeeDTO);
        try {
            System.out.println("Angajatul pe care l-am primit are datele "+employee.getUser()+" "+employee.getPassword());
            Employee employee1= server.login(employee,this);
            if(employee1.getId()>0) {
                return okResponse;
            }
            else {
                System.out.println("Angajatul nu a fost gasit");
                return (new Response.Builder()).type(ResponseType.NOT_FOUND).build();
            }
        } catch (Exception e) {
            this.connected = false;
            System.out.println("Error in login: " + e.getMessage());
            return (new Response.Builder()).type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_FLIGHTS(Request request) {
        try {
            List<Flight> allFlights= StreamSupport.stream(this.server.getAllFlights().spliterator(), false)
                    .toList();
            List<FlightDTO> flights = new ArrayList<>();
            allFlights.forEach(flight -> {
                flights.add(new FlightDTO(flight.getId(), flight.getOrigin(), flight.getDeparture(), flight.getDayTime(), flight.getAvailableSeats(), flight.getAirport()));
            });
            System.out.println(flights.size());
            return (new Response.Builder()).type(ResponseType.RECEIVE_FLIGHTS).data(flights).build();
        } catch (Exception e) {
            System.out.println("Error in get flights: " + e.getMessage());
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ORIGIN(Request request) {
        try {
            Set<String> origin = this.server.getOrigin();
            return (new Response.Builder()).type(ResponseType.RECEIVE_ORIGIN).data(origin).build();
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleGET_DESTINATION(Request request) {
        try {
            Set<String> origin = this.server.getDestination();
            return (new Response.Builder()).type(ResponseType.RECEIVE_DESTINATION).data(origin).build();
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleSEARCH(Request request) {
        try{
            FlightDTO flightDTO = (FlightDTO) request.data();
            Flight flight1 = DTOUtils.getFromDTO(flightDTO);
            List<Flight> allFlights= StreamSupport.stream(this.server.searchFlight(flight1).spliterator(), false)
                    .toList();
            List<FlightDTO> flights = new ArrayList<FlightDTO>();
            allFlights.forEach(flight -> {
                flights.add(new FlightDTO(flight.getId(), flight.getOrigin(), flight.getDeparture(), flight.getDayTime(), flight.getAvailableSeats(), flight.getAirport()));
            });
            return (new Response.Builder()).type(ResponseType.RECEIVE_SEARCH).data(flights).build();
        }
        catch(Exception e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleADD_TICKET(Request request){
        try {
            TicketDTO ticketDTO = (TicketDTO) request.data();
            Ticket ticket = DTOUtils.getFromDTO(ticketDTO);
            server.addTicket(ticket);
            return okResponse;
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    @Override
    public void newTicketBought(Ticket ticket) throws Exception {
        TicketDTO ticketDTO = DTOUtils.getDTO(ticket);
        Response response = (new Response.Builder()).type(ResponseType.NEW_TICKET).data(ticketDTO).build();
        sendResponse(response);
        System.out.println("Am trimis un update client");
    }
}

