package clientesocket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import myevents.clientListennerEvent;
import myevents.clientMsgListenner;
import servidor.Paquete;

/**
 *
 * @author Sergio_W
 */
public class Reconnectar implements Runnable{
    private Thread hilo;
    private Socket client;
    private String name;
    private String ipAdress;
    private int port;
    private volatile boolean state;
     private static ArrayList listeners;

    public Reconnectar(Socket _client, String _name, String _ipAdress, int _port) {
        this.listeners = new ArrayList();
        this.client = _client;
        this.name = _name;
        this.ipAdress = _ipAdress;
        this.port = _port;
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.state = true;
        this.hilo.start();
    }
    
    public void stop(){
        state = false;
        hilo.interrupt();
    }
    
    @Override
    public void run() {
        while (state) {
            try {
                client = new Socket(ipAdress, port);
                Paquete nombreCliente = new Paquete(this.name);
                ObjectOutputStream flujo = new ObjectOutputStream(client.getOutputStream());
                flujo.writeObject(nombreCliente);
                hilo.interrupt();
                this.triggerOnReconnected();
            } catch (IOException iOException) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Reconnectar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
 
    public void addEventListener(clientMsgListenner listener){
        this.listeners.add(listener);
    }
    
    public void triggerOnReconnected(){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            clientMsgListenner listener = (clientMsgListenner) li.next();
            clientListennerEvent clientEvent = new clientListennerEvent(this,this);
            (listener).onReconnected(clientEvent);
        }
    }
}
