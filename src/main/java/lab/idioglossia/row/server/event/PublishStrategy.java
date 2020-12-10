package lab.idioglossia.row.server.event;

import lab.idioglossia.row.server.domain.RowWebsocketSession;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;

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
                if(subscription.info().getSessionId().equals(rowWebsocketSession.getSession().getId())){
                    rowWebsocketSession.getSession().sendMessage(new TextMessage(json));
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
                rowWebsocketSession.getSession().sendMessage(new TextMessage(json));
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
