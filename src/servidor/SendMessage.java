package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Sergio_W
 */
public class SendMessage implements Runnable{
    private ObjectOutputStream flujoSalida;
    private ClientSocket client;
    private Thread hilo;
    private Paquete msg;

    public SendMessage(ClientSocket client, Paquete msg) {
        this.client = client;
        this.msg = msg;
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }

    @Override
    public void run() {
        try {
            this.flujoSalida = new ObjectOutputStream(client.getClient().getOutputStream());
            this.flujoSalida.writeObject(msg);
        } catch (IOException ex) {
            System.err.println("SendMessage Error: " + ex.getMessage());
        }
        this.hilo.stop();
    }
}