package labs.psychogen.row.event;

import labs.psychogen.row.domain.RowWebsocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;

public interface PublishStrategy {
    static PublishStrategy SINGLE_SESSION(){
        return (json, rowWebsocketSession, subscription) -> {
            WebSocketSession session = rowWebsocketSession.getSession();
            if(subscription.info().getSessionId().equals(session.getId())){
                session.sendMessage(new TextMessage(json));
            }
        };
    }

    static PublishStrategy USER_SESSIONS(){
        return (json, rowWebsocketSession, subscription) -> {
            WebSocketSession session = rowWebsocketSession.getSession();
            session.sendMessage(new TextMessage(json));
        };
    }

    void publish(String json, RowWebsocketSession rowWebsocketSession, Subscription subscription) throws IOException;
}
