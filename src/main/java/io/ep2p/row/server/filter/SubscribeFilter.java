package io.ep2p.row.server.filter;

import io.ep2p.row.server.domain.protocol.ResponseDto;
import io.ep2p.row.server.exception.InvalidPathException;
import io.ep2p.row.server.service.SubscriberService;
import io.ep2p.row.server.domain.RowResponseStatus;
import io.ep2p.row.server.domain.protocol.RequestDto;
import io.ep2p.row.server.event.Subscription;
import io.ep2p.row.server.utl.RequestResponseUtil;
import io.ep2p.row.server.ws.RowServerWebsocket;

public class SubscribeFilter implements RowFilter {
    private final SubscriberService subscriberService;
    private final boolean pre;

    public SubscribeFilter(SubscriberService subscriberService, boolean pre) {
        this.subscriberService = subscriberService;
        this.pre = pre;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowServerWebsocket<?> rowServerWebsocket) throws Exception {
        try {
            if(RequestResponseUtil.isUnSubscribing(requestDto)){
                subscriberService.handleUnsubscribe(requestDto, pre);
            } else{
                Subscription subscription = subscriberService.handleSubscription(requestDto, pre);
                if(subscription != null){
                    RequestResponseUtil.addSubscriptionDetails(subscription, responseDto);
                }
            }
        } catch (InvalidPathException e){
            responseDto.setStatus(RowResponseStatus.NOT_FOUND);
        }
        return true;
    }
}
