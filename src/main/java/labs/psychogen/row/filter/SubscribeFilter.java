package labs.psychogen.row.filter;

import labs.psychogen.row.domain.RowResponseStatus;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.event.Subscription;
import labs.psychogen.row.exception.InvalidPathException;
import labs.psychogen.row.service.SubscriberService;
import labs.psychogen.row.utl.RequestResponseUtil;
import org.springframework.web.socket.WebSocketSession;

import static labs.psychogen.row.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;
import static labs.psychogen.row.config.Naming.SUBSCRIPTION_Id_HEADER_NAME;

public class SubscribeFilter implements RowFilter {
    private final SubscriberService subscriberService;
    private final boolean pre;

    public SubscribeFilter(SubscriberService subscriberService, boolean pre) {
        this.subscriberService = subscriberService;
        this.pre = pre;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, WebSocketSession webSocketSession) throws Exception {
        try {
            Subscription subscription = subscriberService.handleSubscription(requestDto, pre);
            if(subscription != null){
                RequestResponseUtil.addHeader(SUBSCRIPTION_EVENT_HEADER_NAME, subscription.event(), responseDto);
                RequestResponseUtil.addHeader(SUBSCRIPTION_Id_HEADER_NAME, subscription.id(), responseDto);
            }
        } catch (InvalidPathException e){
            responseDto.setStatus(RowResponseStatus.NOT_FOUND);
        }
        return true;
    }
}
