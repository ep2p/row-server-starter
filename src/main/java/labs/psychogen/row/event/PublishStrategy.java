package labs.psychogen.row.event;

import labs.psychogen.row.domain.RowWebsocketSession;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;

public interface PublishStrategy {
    @Getter
    enum Strategy {
        SINGLE_SESSION(new PublishStrategy() {
            @Override
            public String name() {
                return "SINGLE_SESSION";
            }

            @Override
            public void publish(String json, RowWebsocketSession rowWebsocketSession, Subscription subscription) throws IOException {
                WebSocketSession session = rowWebsocketSession.getSession();
                if(subscription.info().getSessionId().equals(session.getId())){
                    session.sendMessage(new TextMessage(json));
                }
            }
        }),
        USER_SESSIONS(new PublishStrategy() {
            @Override
            public String name() {
                return "USER_SESSIONS";
            }

            @Override
            public void publish(String json, RowWebsocketSession rowWebsocketSession, Subscription subscription) throws IOException {
                WebSocketSession session = rowWebsocketSession.getSession();
                session.sendMessage(new TextMessage(json));
            }
        });

        private final PublishStrategy publishStrategy;

        Strategy(PublishStrategy publishStrategy) {
            this.publishStrategy = publishStrategy;
        }
    }

    String name();
    void publish(String json, RowWebsocketSession rowWebsocketSession, Subscription subscription) throws IOException;
}
