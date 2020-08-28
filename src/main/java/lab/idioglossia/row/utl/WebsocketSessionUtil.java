package lab.idioglossia.row.utl;

import lab.idioglossia.row.config.Naming;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

public class WebsocketSessionUtil {

    public static String getUserId(WebSocketSession session){
        return (String) session.getAttributes().get(Naming.USER_ID_ATTRIBUTE_NAME);
    }

    public static Date getLastHeartbeat(WebSocketSession session){
        return (Date) session.getAttributes().get(Naming.IN_HEARTBEAT_ATTRIBUTE_NAME);
    }
}
