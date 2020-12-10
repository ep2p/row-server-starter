package lab.idioglossia.row.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lab.idioglossia.row.config.Naming;
import lab.idioglossia.row.context.DefaultContextImpl;
import lab.idioglossia.row.context.RowContextHolder;
import lab.idioglossia.row.context.RowUser;
import lab.idioglossia.row.domain.RowResponseStatus;
import lab.idioglossia.row.domain.RowWebsocketSession;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.domain.protocol.ResponseDto;
import lab.idioglossia.row.filter.RowFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Slf4j
public class ProtocolService {
    private final RowFilterChain rowFilterChain;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ProtocolService(RowFilterChain rowFilterChain) {
        this.rowFilterChain = rowFilterChain;
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void handle(RowWebsocketSession webSocketSession, TextMessage textMessage){
        log.trace("Received message: " + textMessage.getPayload());
        String payload = textMessage.getPayload();
        fillContext(webSocketSession.getSession());
        ResponseDto responseDto = ResponseDto.builder().status(RowResponseStatus.OK.getId()).build();
        String requestId = null;
        try {
            RequestDto requestDto = objectMapper.readValue(payload, RequestDto.class);
            //ignoring payloads that are not request type
            if(requestDto.getType() == null || !requestDto.getType().equals("request")){
                return;
            }
            requestId = requestDto.getId();
            Set<ConstraintViolation<RequestDto>> constraintViolations = validator.validate(requestDto);
            if(constraintViolations.size() > 0){
                responseDto = ResponseDto.builder()
                        .requestId(requestDto.getId())
                        .status(RowResponseStatus.PROTOCOL_ERROR.getId())
                        .build();
            }else {
                responseDto.setRequestId(requestId);
                rowFilterChain.filter(requestDto, responseDto, webSocketSession);
            }
        } catch (JsonProcessingException e) {
            responseDto = ResponseDto.builder()
                    .status(RowResponseStatus.OTHER.getId())
                    .requestId(requestId)
                    .build();
            log.error("Json Error", e);
        } catch (Exception e) {
            responseDto.setStatus(RowResponseStatus.INTERNAL_SERVER_ERROR);
            log.error("Exception thrown while handling message", e);
        }

        try {
            String responsePayload = objectMapper.writeValueAsString(responseDto);
            log.trace("Sending response: "+ responsePayload);
            webSocketSession.getSession().sendMessage(new TextMessage(responsePayload));
        } catch (IOException e) {
            log.error("Failed to publish response to websocket", e);
        }
    }

    private void fillContext(WebSocketSession webSocketSession) {
        RowContextHolder.setContext(
                new DefaultContextImpl(
                        new RowUser((String) webSocketSession.getAttributes().get(Naming.USER_ID_ATTRIBUTE_NAME), webSocketSession.getId()), true
                )
        );
    }
}
