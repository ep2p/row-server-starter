package lab.idioglossia.row.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.ws.RowServerWebsocket;

import java.io.IOException;

public class RawMessagePublisherService {
    private final ObjectMapper objectMapper;

    public RawMessagePublisherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void publish(RowServerWebsocket<?> rowServerWebsocket, Object message) throws IOException {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setBody(message);
        rowServerWebsocket.sendTextMessage(objectMapper.writeValueAsString(responseDto));
    }

    public void publish(RowServerWebsocket<?> rowServerWebsocket, String message) throws IOException {
        ResponseDto responseDto = new ResponseDto();
        JsonNode jsonNode = objectMapper.valueToTree(message);
        responseDto.setBody(jsonNode);
        rowServerWebsocket.sendTextMessage(objectMapper.writeValueAsString(responseDto));
    }

}
