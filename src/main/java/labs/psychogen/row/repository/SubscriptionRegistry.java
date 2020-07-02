package labs.psychogen.row.repository;

import labs.psychogen.row.event.Subscription;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public interface SubscriptionRegistry {
    void addSubscription(Subscription subscription);
    void removeSubscription(Subscription subscription);
    Set<Subscription> getSubscriptions(String event);

    class RowSubscriptionRegistry implements SubscriptionRegistry {
        private final Map<String, Set<Subscription>> eventSubscribers = new HashMap<>();

        @Override
        public synchronized void addSubscription(Subscription subscription) {
            Set<Subscription> subscriptions = eventSubscribers.computeIfAbsent(subscription.event(), s -> {
                return new CopyOnWriteArraySet<>();
            });
            subscriptions.add(subscription);
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
    }
}
