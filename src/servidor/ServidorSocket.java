package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server_events.ClientEvent;
import server_events.ClientEventListener;

/**
 *
 * @author Programacion
 */
public class ServidorSocket implements ClientEventListener{
    protected ServerSocket server;
    protected int puerto;
    protected ClientListener clientListener;
    protected LinkedList<ClientSocket> clients;
    protected ClientPing stillConnected;
    
    public ServidorSocket(int puerto){
        this.puerto = puerto;
        this.clients = new LinkedList<>();
        this.stillConnected = new ClientPing(clients);
    }
    
    public void startServer(){
        try {
            this.server = new ServerSocket(this.puerto);
            this.clientListener = new ClientListener(server);
            this.clientListener.addEventListener(this);
            this.clientListener.start();
            this.stillConnected.start();
            System.out.println("Se inicio el Servidor...");
        } catch (Exception ex) {
            System.err.println("ServidorSocket Constructor Error: " + ex.getMessage());
        }
    }
    
    public void stopServer(){
        try {
            stopClients();
            this.clientListener.stopClientListener();
            this.server.close();
            System.out.println("El Servidor se detuvo...");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void stopClients() {
        for (ClientSocket client : this.clients) {
            client.stop();
        }
        this.clients.clear();
    }
    
    public void sendMessage(ClientSocket client, Paquete msg){
        SendMessage send = new SendMessage(client, msg);
        send.start();
    }
    
    public void sendMessageAll(Paquete msg){
        for (ClientSocket client : clients) {
            sendMessage(client, msg);
        }
    }
    
    private ClientSocket findClient(String clientReceptor) {
        for (int i = 0; i < this.clients.size(); i++) {
            if (clientReceptor.equalsIgnoreCase(this.clients.get(i).getClientName())) {
                return this.clients.get(i);
            }
        }
        return null;
    }
    
    private LinkedList<String> listClient() {
        LinkedList<String> clientes = new LinkedList<>();
        clients.stream().forEach((client) -> {
            clientes.add(client.getClientName());
        });
        return clientes;
    }

    private boolean exitsClient(String clientName) {
        for (ClientSocket client : clients) {
            if (client.getClientName().equalsIgnoreCase(clientName)) {
                return true;
            }
        }
        return false;
    }
    
    //EVENTOS DE LA CLASE INTERFACE--------------------------------------------------
    
    @Override
    public void onConnected(ClientEvent ev) {
        if (this.clients.isEmpty() || !exitsClient(ev.getClientSocket().getClientName())) {
            ev.getClientSocket().addEventListener(this);
            ev.getClientSocket().start();
            onServerConnected(ev.getClientSocket());
            this.clients.add(ev.getClientSocket());
            
            LinkedList<String> clientes = listClient();
            Paquete msg1 = new Paquete(clientes);
            sendMessage(ev.getClientSocket(), msg1);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            Paquete msg = new Paquete(ev.getClientSocket().getClientName(), Paquete.Tipo.ADD);
            sendMessageAll(msg);
        }else{
            Paquete smg = new Paquete();
            smg.setTipo(Paquete.Tipo.EXIST);
            smg.setText(ev.getClientSocket().getClientName());
            sendMessage(ev.getClientSocket(), smg);
        }
    }

    @Override
    public void onDesconnected(ClientEvent ev) {
        for (int i = 0; i < this.clients.size(); i++) {
            String name = this.clients.get(i).getClientName();
            if (ev.getClientSocket().getClientName().equalsIgnoreCase(name)) {
                //this.clients.get(i).stop();
                this.clients.remove(i);
            }
        }
        onServerDesconnected(ev.getClientSocket());
        Paquete msg = new Paquete(ev.getClientSocket().getClientName(), Paquete.Tipo.DELETE);
        sendMessageAll(msg);
    }

    @Override
    public void onRead(ClientEvent ev) {
        switch (ev.getMsg().getTipo()) {
            case SERVER:
                getMessage(ev.getClientSocket(), ev.getMsg().getText());
                break;
            case CLIENT:
                ClientSocket receptor = findClient(ev.getMsg().getClientReceptor());
                sendMessage(receptor, ev.getMsg());
                break;
            case LIST:
                LinkedList<String> clientes = listClient();
                Paquete msg = new Paquete(clientes);
                sendMessage(ev.getClientSocket(), msg);
                break;
            default:
                throw new AssertionError();
        }
    }

    public void getMessage(ClientSocket clientSocket, String text) {
        //Do something
    }

    public void onServerDesconnected(ClientSocket clientSocket) {
        //Do something
    }

    public void onServerConnected(ClientSocket clientSocket) {
        //Do something
    }
}
