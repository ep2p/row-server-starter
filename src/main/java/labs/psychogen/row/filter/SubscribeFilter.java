package labs.psychogen.row.filter;

import labs.psychogen.row.domain.RowResponseStatus;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.exception.InvalidPathException;
import labs.psychogen.row.service.SubscriberService;
import org.springframework.web.socket.WebSocketSession;

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
            subscriberService.handleSubscription(requestDto, pre);
        } catch (InvalidPathException e){
            responseDto.setStatus(RowResponseStatus.NOT_FOUND);
        }
        return true;
    }
}
