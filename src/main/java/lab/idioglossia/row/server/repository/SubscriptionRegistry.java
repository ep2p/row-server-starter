package lab.idioglossia.row.server.repository;

import lab.idioglossia.row.server.event.Subscription;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public interface SubscriptionRegistry {
    boolean addSubscription(Subscription subscription);
    void removeSubscription(Subscription subscription);
    Set<Subscription> getSubscriptions(String event);
    void remove(String userId, @Nullable String sessionId);
    void unsubscribe(String event, String subscriptionId);

    class RowSubscriptionRegistry implements SubscriptionRegistry {
        private final Map<String, Set<Subscription>> eventSubscribers = new HashMap<>();

        @Override
        public synchronized boolean addSubscription(Subscription subscription) {
            Set<Subscription> subscriptions = eventSubscribers.computeIfAbsent(subscription.event(), s -> {
                return new CopyOnWriteArraySet<>();
            });
            return subscriptions.add(subscription);
        }

        @Override
        public synchronized void removeSubscription(Subscription subscription) {
            eventSubscribers.computeIfPresent(subscription.event(),(s, subscriptions) -> {
                subscriptions.remove(subscription);
                return subscriptions;
            });
        }

        @Override
        public Set<Subscription> getSubscriptions(String event) {
            return eventSubscribers.getOrDefault(event, new HashSet<>());
        }

        @Override
        public void remove(String userId, @Nullable String sessionId) {
            eventSubscribers.values().forEach(subscriptions -> {
                for (Subscription subscription : subscriptions) {
                    if(subscription.info().getUserId().equals(userId)){
                        if(sessionId != null && sessionId.equals(subscription.info().getSessionId()))
                            subscriptions.remove(subscription);
                        else if(sessionId == null)
                            subscriptions.remove(subscription);
                    }
                }
            });
        }

        @Override
        public void unsubscribe(String event, String subscriptionId) {
            Set<Subscription> subscriptions = eventSubscribers.get(event);
            if(subscriptions == null)
                return;
            subscriptions.removeIf(subscription -> subscription.id().equals(subscriptionId));
        }
    }
}
