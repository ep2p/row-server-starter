package labs.psychogen.row.event;

import labs.psychogen.row.domain.RowWebsocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;

public interface PublishStrategy {
    enum Strategy {
        SINGLE_SESSION((json, rowWebsocketSession, subscription) -> {
            WebSocketSession session = rowWebsocketSession.getSession();
            if(subscription.info().getSessionId().equals(session.getId())){
                session.sendMessage(new TextMessage(json));
            }
        }), USER_SESSIONS((json, rowWebsocketSession, subscription) -> {
            WebSocketSession session = rowWebsocketSession.getSession();
            session.sendMessage(new TextMessage(json));
        });
        private final PublishStrategy publishStrategy;

        Strategy(PublishStrategy publishStrategy) {
            this.publishStrategy = publishStrategy;
        }
    }

    void publish(String json, RowWebsocketSession rowWebsocketSession, Subscription subscription) throws IOException;
}
