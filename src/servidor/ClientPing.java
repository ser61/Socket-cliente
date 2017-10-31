package servidor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import server_events.ClientEvent;
import server_events.ClientEventListener;

/**
 *
 * @author Estudiante
 */
public class ClientPing implements Runnable{
    private LinkedList<ClientSocket> listClients;
    private static ArrayList listeners;
    private boolean state;
    private Thread hilo;

    public ClientPing(LinkedList<ClientSocket> listClients) {
        this.listClients = listClients;
        this.listeners = new ArrayList();
        this.state = true;
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }
    
    public void stopt(){
        this.state = false;
        this.hilo.interrupt();
    }
    
    @Override
    public void run() {
        while (this.state) {
            if (!this.listClients.isEmpty()) {
                for (ClientSocket listClient : listClients) {
                    try {
                        if (listClient.getClient().getInetAddress().isReachable(500)) {
                            triggerOnConnected(listClient);
                        }
                    } catch (IOException ex) {
                        System.err.println("ClientPing Error: " + ex.getMessage());
                    }
                }
            }
        }
    }
    
    public void addEventListener(ClientEventListener listener){
        this.listeners.add(listener);
    }
    
    public void triggerOnConnected(ClientSocket client){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            ClientEventListener listener = (ClientEventListener) li.next();
            ClientEvent clientEvent = new ClientEvent(this, client);
            (listener).onConnected(clientEvent);
        }
    }
}
