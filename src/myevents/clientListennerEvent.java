package myevents;

import clientesocket.Reconnectar;
import clientesocket.ServidorListenner;
import java.util.EventObject;
import servidor.Paquete;

/**
 *
 * @author Sergio_W
 */
public class clientListennerEvent extends EventObject{
    private ServidorListenner servidorListenner;
    private Reconnectar rec;
    private Paquete msg;

    public clientListennerEvent(Object source, ServidorListenner _servidorListenner) {
        super(source);
        this.servidorListenner = _servidorListenner;
    }

    public Reconnectar getRec() {
        return rec;
    }
    
    public clientListennerEvent(Object o, Reconnectar _rec) {
        super(o);
        this.rec = _rec;
    }
    

    public ServidorListenner getServidorListenner() {
        return servidorListenner;
    }

    public void setServidorListenner(ServidorListenner servidorListenner) {
        this.servidorListenner = servidorListenner;
    }

    public Paquete getMsg() {
        return msg;
    }

    public void setMsg(Paquete text) {
        this.msg = text;
    }
    
}
