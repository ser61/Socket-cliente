package servidor;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Sergio_W
 */

public class Paquete implements Serializable{
    public static enum Tipo {SERVER, CLIENT, LIST, ADD, DELETE, EXIST};
    private String text;
    private String client;
    private String clientReceptor;
    private LinkedList<String> list;
    private Tipo tipo;

    public Paquete() {
    }
    
    public Paquete(String text) {
        this.text = text;
        this.tipo = Tipo.SERVER;
    }

    public Paquete(String _clientReceptor, String text) {
        this.clientReceptor = _clientReceptor;
        this.text = text;
        this.tipo = Tipo.CLIENT;
    }

    public Paquete(LinkedList<String> clients) {
        this.list = clients;
        this.tipo = Tipo.LIST;
    }

    public Paquete(String client, Tipo tipo) {
        this.client = client;
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCliente() {
        return client;
    }

    public void setCliente(String cliente) {
        this.client = cliente;
    }

    public LinkedList<String> getClients() {
        return list;
    }

    public String getClientReceptor() {
        return clientReceptor;
    }

    public void setClientReceptor(String clientReceptor) {
        this.clientReceptor = clientReceptor;
    }

    public void setClients(LinkedList<String> clients) {
        this.list = clients;
    }
}
