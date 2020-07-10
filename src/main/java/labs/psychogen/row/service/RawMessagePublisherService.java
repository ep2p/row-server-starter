package labs.psychogen.row.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.domain.protocol.ResponseDto;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class RawMessagePublisherService {
    private final ObjectMapper objectMapper;

    public RawMessagePublisherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void publish(WebSocketSession webSocketSession, Object message) throws IOException {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setBody(message);
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseDto)));
    }

    public void publish(WebSocketSession webSocketSession, String message) throws IOException {
        ResponseDto responseDto = new ResponseDto();
        JsonNode jsonNode = objectMapper.valueToTree(message);
        responseDto.setBody(jsonNode);
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseDto)));
    }

}
