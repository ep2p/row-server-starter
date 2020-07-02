package labs.psychogen.row.service;

import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SubscriptionRegistry;

import java.io.IOException;

public class PublisherService {
    private final SubscriptionRegistry subscriptionRegistry;
    private final RowSessionRegistry sessionRegistry;

    public PublisherService(SubscriptionRegistry subscriptionRegistry, RowSessionRegistry sessionRegistry) {
        this.subscriptionRegistry = subscriptionRegistry;
        this.sessionRegistry = sessionRegistry;
    }

    public final void publish(String event, String json){
        subscriptionRegistry.getSubscriptions(event).forEach(subscription -> {
            sessionRegistry.getSessions(subscription.info().getUserId()).forEach(rowWebsocketSession -> {
                try {
                    subscription.info().getStrategy().publish(json, rowWebsocketSession, subscription);
                } catch (IOException e) {
                    if(!rowWebsocketSession.getSession().isOpen()){
                        subscriptionRegistry.removeSubscription(subscription);
                    }
                    e.printStackTrace(); //todo
                }
            });
        });
    }

}
