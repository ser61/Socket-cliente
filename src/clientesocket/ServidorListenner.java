package clientesocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import myevents.clientListennerEvent;
import myevents.clientMsgListenner;
import servidor.Paquete;

/**
 *
 * @author Sergio_W
 */
public class ServidorListenner implements Runnable{
    private boolean state;
    private Socket client;
    private static ArrayList listeners;
    private Thread hilo;

    public ServidorListenner(Socket _client) {
        this.client = _client;
        this.state = true;
        this.listeners = new ArrayList();
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }
    
    public void stop(){
        this.state = false;
        this.hilo.stop();
    }

    @Override
    public void run() {
        try {
            while (this.state) {
                ObjectInputStream flujoEntrada = new ObjectInputStream(client.getInputStream());
                Paquete msg = (Paquete)flujoEntrada.readObject();
                this.triggerReadMsg(msg);
            }
        } catch (IOException ex) {
            this.triggerDesconnected(this);
        } catch (ClassNotFoundException ex) {
            System.err.println("ServidorListenner Error Msg: " + ex.getMessage());
        }finally{
            //Linea que se debe ejecutar siempre...
        }
    }
    
    public void addEventListener(clientMsgListenner listener){
        this.listeners.add(listener);
    }
    
    public void triggerReadMsg(Paquete msg){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            clientMsgListenner listener = (clientMsgListenner) li.next();
            clientListennerEvent clientEvent = new clientListennerEvent(this,this);
            clientEvent.setMsg(msg);
            (listener).onRead(clientEvent);
        }
    }
    
    public void triggerDesconnected(ServidorListenner client){
        ListIterator li = this.listeners.listIterator();
        while(li.hasNext()){
            clientMsgListenner listener = (clientMsgListenner) li.next();
            clientListennerEvent clientEvent = new clientListennerEvent(this, client);
            (listener).onDesconnected(clientEvent);
        }
    }
}