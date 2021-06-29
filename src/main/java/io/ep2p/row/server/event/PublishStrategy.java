package io.ep2p.row.server.event;

import io.ep2p.row.server.ws.RowServerWebsocket;
import lombok.Getter;

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
            public void publish(String json, RowServerWebsocket<?> rowServerWebsocket, Subscription subscription) throws IOException {
                if(subscription.info().getSessionId().equals(rowServerWebsocket.getId())){
                    rowServerWebsocket.sendTextMessage(json);
                }
            }
        }),
        USER_SESSIONS(new PublishStrategy() {
            @Override
            public String name() {
                return "USER_SESSIONS";
            }

            @Override
            public void publish(String json, RowServerWebsocket<?> rowServerWebsocket, Subscription subscription) throws IOException {
                rowServerWebsocket.sendTextMessage(json);
            }
        });

        private final PublishStrategy publishStrategy;

        Strategy(PublishStrategy publishStrategy) {
            this.publishStrategy = publishStrategy;
        }
    }

    String name();
    void publish(String json, RowServerWebsocket<?> rowServerWebsocket, Subscription subscription) throws IOException;
}
