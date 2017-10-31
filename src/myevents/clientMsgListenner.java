package myevents;

import java.util.EventListener;
import org.w3c.dom.events.Event;

/**
 *
 * @author Sergio_W
 */
public interface clientMsgListenner extends EventListener{
    
    public abstract void onRead(clientListennerEvent ev);
    
    public abstract void onDesconnected(clientListennerEvent ev);
    
    public abstract void onReconnected(clientListennerEvent ev);
}
