package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import server_events.ClientEvent;
import server_events.ClientEventListener;

/**
 *
 * @author Programacion
 */
public class ClientListener extends Thread{
    private static ArrayList listeners;
    private boolean         state;
    private ServerSocket    server;
    private Thread          hilo;
    
    public ClientListener(ServerSocket server) {
        this.server     = server;
        this.state      = true;
        this.listeners  = new ArrayList();
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }
    
    public void stopClientListener(){
        this.hilo.interrupt();
        this.state = false;
    }
    
    @Override
    public void run() {
        while (this.state) {                
            try {
                Socket client;
                //Escucha a algun cliente
                client = this.server.accept();
                //Se crea un canal de recepcion de msg tipo Object
                ObjectInputStream flujoEntrada = new ObjectInputStream(client.getInputStream());
                //Casting al Objeto recibido
                Paquete clientName = (Paquete)flujoEntrada.readObject();
                String name = clientName.getText();
                //Disparador de conexion
                ClientSocket newClient = new ClientSocket(client, true, name);
                this.triggerOnConnected(newClient);
            } catch (IOException ex) {
                break;
            } catch (ClassNotFoundException ex) {
                System.err.println("ClientListener error: " + ex.getMessage());
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
            ClientEvent clientEvent;
            clientEvent = new ClientEvent(this, client);
            (listener).onConnected(clientEvent);
        }
    }
}
