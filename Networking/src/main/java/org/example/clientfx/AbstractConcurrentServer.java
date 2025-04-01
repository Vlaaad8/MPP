package org.example.clientfx;

import java.net.Socket;
import java.sql.SQLOutput;

public abstract class AbstractConcurrentServer extends AbstractServer {
    public AbstractConcurrentServer(int port){
        super(port);
    }
    @Override
    public void processRequest(Socket client) {
        System.out.println("Am inceput sa lucrez la procesarea requestului");
        Thread threadWorker=createWorker(client);
        threadWorker.start();
    }
    public abstract Thread createWorker(Socket client);
}
