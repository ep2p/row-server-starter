package lab.idioglossia.row.service;

import lab.idioglossia.row.RowEndpoint;
import lab.idioglossia.row.annotations.PostSubscribe;
import lab.idioglossia.row.annotations.PreSubscribe;
import lab.idioglossia.row.context.RowContextHolder;
import lab.idioglossia.row.context.RowUser;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.event.PublishStrategy;
import lab.idioglossia.row.event.Subscription;
import lab.idioglossia.row.exception.InvalidPathException;
import lab.idioglossia.row.repository.SubscriptionRegistry;
import lab.idioglossia.row.utl.RequestResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import static lab.idioglossia.row.config.Naming.SUBSCRIPTION_Id_HEADER_NAME;

@Slf4j
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
            log.debug("New subscription added for "+ subscription.event() + " with info: " + subscription.info());
            return subscription;
        }
        return null;
    }

    public final Subscription handleSubscription(RequestDto requestDto, boolean pre) throws InvalidPathException {
        RowEndpoint matchingEndpoint = endpointProvider.getMatchingEndpoint(RowEndpoint.RowMethod.valueOf(requestDto.getMethod()), requestDto.getAddress());
        RowUser rowUser = RowContextHolder.getContext().getRowUser();
        Subscription subscription = null;

        if(pre){
            PreSubscribe preSubscribe = matchingEndpoint.getPreSubscribe();
            if(preSubscribe != null)
                subscription = subscribe(preSubscribe.value(), preSubscribe.strategy().getPublishStrategy(), rowUser.getUserId(), rowUser.getSessionId());
        }else {
            PostSubscribe postSubscribe = matchingEndpoint.getPostSubscribe();
            if(postSubscribe != null)
                subscription = subscribe(postSubscribe.value(), postSubscribe.strategy().getPublishStrategy(), rowUser.getUserId(), rowUser.getSessionId());
        }

        return subscription;
    }

    public void handleUnsubscribe(RequestDto requestDto, boolean pre) throws InvalidPathException {
        RowEndpoint matchingEndpoint = endpointProvider.getMatchingEndpoint(RowEndpoint.RowMethod.valueOf(requestDto.getMethod()), requestDto.getAddress());
        String event = null;
        if(pre && matchingEndpoint.getPreSubscribe() != null){
            event = matchingEndpoint.getPreSubscribe().value();
        }else if(matchingEndpoint.getPostSubscribe() != null){
            event = matchingEndpoint.getPostSubscribe().value();
        }
        if(event != null)
            subscriptionRegistry.unsubscribe(event, RequestResponseUtil.getHeaderValue(SUBSCRIPTION_Id_HEADER_NAME, requestDto));
    }

    private static String subscriptionIdGenerator(String event, String userId, String sessionId){
        String id = event + "." + userId + "." + sessionId;
        return DigestUtils.md5DigestAsHex(id.getBytes()).substring(0, Math.min(id.length(), 10));
    }
}
