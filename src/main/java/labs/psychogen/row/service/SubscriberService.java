package labs.psychogen.row.service;

import labs.psychogen.row.RowEndpoint;
import labs.psychogen.row.annotations.PostSubscribe;
import labs.psychogen.row.annotations.PreSubscribe;
import labs.psychogen.row.context.RowContextHolder;
import labs.psychogen.row.context.RowUser;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.event.PublishStrategy;
import labs.psychogen.row.event.Subscription;
import labs.psychogen.row.exception.InvalidPathException;
import labs.psychogen.row.repository.SubscriptionRegistry;

import java.util.Base64;

public class SubscriberService {
    private final SubscriptionRegistry subscriptionRegistry;
    private final EndpointProvider endpointProvider;

    public SubscriberService(SubscriptionRegistry subscriptionRegistry, EndpointProvider endpointProvider) {
        this.subscriptionRegistry = subscriptionRegistry;
        this.endpointProvider = endpointProvider;
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
                        .strategy(strategy)
                        .build();
            }

            @Override
            public String id() {
                return subscriptionIdGenerator(event, userId, sessionId);
            }

            @Override
            public boolean equals(Object obj) {
                if(obj == null)
                    return false;
                if(obj instanceof Subscription){
                    return ((Subscription) obj).event().equals(this.event())
                            && ((Subscription) obj).info().getStrategy().name().equals(this.info().getStrategy().name())
                            && ((Subscription) obj).info().getStrategy().name().equals("SINGLE_SESSION") ?
                            ((Subscription) obj).info().getUserId().equals(this.info().getUserId()) : ((Subscription) obj).info().getSessionId().equals(this.info().getSessionId());
                }
                return false;
            }
        };
        if (subscriptionRegistry.addSubscription(subscription)) {
            return subscription;
        }
        return null;
    }

    public final void handleSubscription(RequestDto requestDto, boolean pre) throws InvalidPathException {
        RowEndpoint matchingEndpoint = endpointProvider.getMatchingEndpoint(RowEndpoint.RowMethod.valueOf(requestDto.getMethod()), requestDto.getAddress());
        RowUser rowUser = RowContextHolder.getContext().getRowUser();
        if(pre){
            PreSubscribe preSubscribe = matchingEndpoint.getPreSubscribe();
            if(preSubscribe != null)
                subscribe(preSubscribe.value(), preSubscribe.strategy().getPublishStrategy(), rowUser.getUserId(), rowUser.getSessionId());
        }else {
            PostSubscribe postSubscribe = matchingEndpoint.getPostSubscribe();
            if(postSubscribe != null)
                subscribe(postSubscribe.value(), postSubscribe.strategy().getPublishStrategy(), rowUser.getUserId(), rowUser.getSessionId());
        }
    }

    private String subscriptionIdGenerator(String event, String userId, String sessionId){
        String id = event + "." + userId + "." + sessionId;
        return Base64.getEncoder().encodeToString(id.getBytes());
    }

}
