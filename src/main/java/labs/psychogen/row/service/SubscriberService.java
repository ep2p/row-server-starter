package labs.psychogen.row.service;

import labs.psychogen.row.event.Subscription;
import labs.psychogen.row.event.PublishStrategy;
import labs.psychogen.row.repository.SubscriptionRegistry;

public class SubscriberService {
    private final SubscriptionRegistry subscriptionRegistry;

    public SubscriberService(SubscriptionRegistry subscriptionRegistry) {
        this.subscriptionRegistry = subscriptionRegistry;
    }

    public final Subscription subscribe(String event, PublishStrategy strategy, String userId, String sessionId){
        Subscription subscription = new Subscription() {
            @Override
            public void close() {
                subscriptionRegistry.removeSubscription(this);
            }

            @Override
            public String event() {
                return event;
            }

            @Override
            public Info info() {
                return Info.builder()
                        .sessionId(sessionId)
                        .userId(userId)
                        .build();
            }
        };
        subscriptionRegistry.addSubscription(subscription);
        return subscription;
    }

}
