package lab.idioglossia.row.server.utl;

import lab.idioglossia.row.server.config.Naming;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

public class WebsocketSessionUtil {

    public static String getUserId(WebSocketSession session){
        return (String) session.getAttributes().get(Naming.USER_ID_ATTRIBUTE_NAME);
    }

    public static Date getLastHeartbeat(WebSocketSession session){
        return (Date) session.getAttributes().get(Naming.IN_HEARTBEAT_ATTRIBUTE_NAME);
    }

    public static <E> E getExtra(WebSocketSession session, Class<E> eClass){
        return eClass.cast(session.getAttributes().get(Naming.EXTRA_ATTRIBUTE_NAME));
    }

    public static Object getExtra(WebSocketSession session){
        return session.getAttributes().get(Naming.EXTRA_ATTRIBUTE_NAME);
    }
}
