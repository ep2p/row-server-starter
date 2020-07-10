package labs.psychogen.row.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SubscriptionRegistry;
import labs.psychogen.row.utl.RequestResponseUtil;

import java.io.IOException;

import static labs.psychogen.row.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;

public class PublisherService {
    private final SubscriptionRegistry subscriptionRegistry;
    private final RowSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public PublisherService(SubscriptionRegistry subscriptionRegistry, RowSessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.subscriptionRegistry = subscriptionRegistry;
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    public final void publish(String event, Object message){
        subscriptionRegistry.getSubscriptions(event).forEach(subscription -> {
            sessionRegistry.getSessions(subscription.info().getUserId()).forEach(rowWebsocketSession -> {
                try {
                    String json = objectMapper.writeValueAsString(getResponse(message, event));
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

    private ResponseDto getResponse(Object message, String event) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setBody(message);
        RequestResponseUtil.addHeader(SUBSCRIPTION_EVENT_HEADER_NAME, event, responseDto);
        return responseDto;
    }

}
