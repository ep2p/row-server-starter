package lab.idioglossia.row.server.filter;

import lab.idioglossia.row.server.domain.RowResponseStatus;
import lab.idioglossia.row.server.domain.RowWebsocketSession;
import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.event.Subscription;
import lab.idioglossia.row.server.exception.InvalidPathException;
import lab.idioglossia.row.server.service.SubscriberService;
import lab.idioglossia.row.server.utl.RequestResponseUtil;

public class SubscribeFilter implements RowFilter {
    private final SubscriberService subscriberService;
    private final boolean pre;

    public SubscribeFilter(SubscriberService subscriberService, boolean pre) {
        this.subscriberService = subscriberService;
        this.pre = pre;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception {
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
