package lab.idioglossia.row.server.event;

import lombok.*;

public interface Subscription {
    void close();
    String event();
    Info info();
    String id();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @ToString
    class Info {
        private PublishStrategy strategy;
        private String userId;
        private String sessionId;
    }
}
