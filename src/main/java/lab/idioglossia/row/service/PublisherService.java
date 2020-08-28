package lab.idioglossia.row.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.row.domain.protocol.ResponseDto;
import lab.idioglossia.row.repository.RowSessionRegistry;
import lab.idioglossia.row.repository.SubscriptionRegistry;
import lab.idioglossia.row.utl.RequestResponseUtil;

import java.io.IOException;

import static lab.idioglossia.row.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;

public class PublisherService {
    private final SubscriptionRegistry subscriptionRegistry;
    private final RowSessionRegistry rowSessionRegistry;
    private final ObjectMapper objectMapper;

    public PublisherService(SubscriptionRegistry subscriptionRegistry, RowSessionRegistry rowSessionRegistry, ObjectMapper objectMapper) {
        this.subscriptionRegistry = subscriptionRegistry;
        this.rowSessionRegistry = rowSessionRegistry;
        this.objectMapper = objectMapper;
    }

    public final void publish(String event, Object message){
        subscriptionRegistry.getSubscriptions(event).forEach(subscription -> {
            rowSessionRegistry.getSessions(subscription.info().getUserId()).forEach(rowWebsocketSession -> {
                try {
                    String json = objectMapper.writeValueAsString(getResponse(message, event));
                    subscription.info().getStrategy().publish(json, rowWebsocketSession, subscription);
                } catch (IOException | IllegalStateException e) {
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
