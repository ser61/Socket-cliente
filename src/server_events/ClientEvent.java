
package server_events;

import java.util.EventObject;
import servidor.ClientSocket;
import servidor.Paquete;

/**
 *
 * @author Programacion
 */
public class ClientEvent extends EventObject{
    private ClientSocket client;
    private Paquete msg;
    
    public ClientEvent(Object o, ClientSocket _cliente) {
        super(o);
        this.client = _cliente;
    }

    public void setMsg(Paquete _msg){
        this.msg = _msg;
    }
    
    public ClientSocket getClientSocket(){
        return this.client;
    }
    
    public Paquete getMsg(){
        return this.msg;
    }
}
