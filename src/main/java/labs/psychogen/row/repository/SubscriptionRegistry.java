package labs.psychogen.row.repository;

import labs.psychogen.row.event.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public interface SubscriptionRegistry {
    void addSubscription(Subscription subscription);
    void removeSubscription(Subscription subscription);
    List<Subscription> getSubscriptions(String event);

    class RowSubscriptionRegistry implements SubscriptionRegistry {
        private final Map<String, List<Subscription>> eventSubscribers = new HashMap<>();

        @Override
        public synchronized void addSubscription(Subscription subscription) {
            List<Subscription> subscriptions = eventSubscribers.computeIfAbsent(subscription.event(), s -> {
                return new CopyOnWriteArrayList<>();
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
        public List<Subscription> getSubscriptions(String event) {
            return eventSubscribers.getOrDefault(event, new ArrayList<>());
        }
    }
}
