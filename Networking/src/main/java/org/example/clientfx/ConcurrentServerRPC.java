package org.example.clientfx;

import org.example.clientfx.Communication.ReflectionWorkerRPC;

import java.net.Socket;

public class ConcurrentServerRPC extends AbstractConcurrentServer{
    private IServices server;

    public ConcurrentServerRPC(int port,IServices server) {
        super(port);
        this.server = server;
    }

    @Override
    public Thread createWorker(Socket client) {
        System.out.println("Am creat un worker");
        ReflectionWorkerRPC worker=new ReflectionWorkerRPC(server,client);
        Thread thread=new Thread(worker);
        return thread;
    }
    @Override
    public void stop(){
        System.out.println("Closing services");
    }
}
