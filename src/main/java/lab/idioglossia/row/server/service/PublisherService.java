package lab.idioglossia.row.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.repository.RowSessionRegistry;
import lab.idioglossia.row.server.repository.SubscriptionRegistry;
import lab.idioglossia.row.server.utl.RequestResponseUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static lab.idioglossia.row.server.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;

@Slf4j
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
                    if(!rowWebsocketSession.isOpen()){
                        subscriptionRegistry.removeSubscription(subscription);
                    }
                    log.error("Failed to publish message", e); //todo: maybe throw as runtime error?
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
