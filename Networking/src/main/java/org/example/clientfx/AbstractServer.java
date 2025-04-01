package org.example.clientfx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class AbstractServer {
    private int port;
    private ServerSocket serverSocket=null;
    public AbstractServer(int port) {
        this.port = port;
    }
    public void start(){
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Am acceptat clientul");
                processRequest(client);
            }
        }
        catch(IOException e){
            System.out.printf("error");
        }
        finally{
            stop();
        }
    }
    public abstract void processRequest(Socket client);
    public void stop(){
        try{
            serverSocket.close();
        }
        catch (IOException e){
            System.out.printf("error");
        }
    }
}
