package labs.psychogen.row.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public interface Subscription {
    void close();
    String event();
    Info info();


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    class Info {
        private PublishStrategy strategy;
        private String userId;
        private String sessionId;
    }
}
