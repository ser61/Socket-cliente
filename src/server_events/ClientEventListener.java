package server_events;

import java.util.EventListener;

/**
 *
 * @author Programacion
 */
public interface ClientEventListener extends EventListener{
    
    public abstract void onConnected(ClientEvent ev);
    
    public abstract void onDesconnected(ClientEvent ev);
    
    public abstract void onRead(ClientEvent ev);
    
}
