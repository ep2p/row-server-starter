package labs.psychogen.row.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.domain.RowResponseStatus;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.exception.InvalidPathException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Log4j2
public class ProtocolService {
    private final RowInvokerService rowInvokerService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ProtocolService(RowInvokerService rowInvokerService) {
        this.rowInvokerService = rowInvokerService;
        objectMapper = new ObjectMapper();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void handle(WebSocketSession webSocketSession, TextMessage textMessage){
        String payload = textMessage.getPayload();
        ResponseDto responseDto;
        String requestId = null;
        try {
            RequestDto requestDto = objectMapper.readValue(payload, RequestDto.class);
            requestId = requestDto.getId();
            Set<ConstraintViolation<RequestDto>> constraintViolations = validator.validate(requestDto);
            if(constraintViolations.size() > 0){
                responseDto = ResponseDto.builder()
                        .requestId(requestDto.getId())
                        .status(RowResponseStatus.PROTOCOL_ERROR.getId())
                        .build();
            }else {
                Object result = rowInvokerService.invoke(requestDto);
                responseDto = ResponseDto.builder()
                        .status(RowResponseStatus.OK.getId())
                        .requestId(requestDto.getId())
                        .body(result)
                        .build();
            }
        } catch (JsonProcessingException e) {
            responseDto = ResponseDto.builder()
                    .status(RowResponseStatus.OTHER.getId())
                    .requestId(requestId)
                    .build();
        } catch (InvalidPathException e) {
            responseDto = ResponseDto.builder()
                    .status(RowResponseStatus.NOT_FOUND.getId())
                    .requestId(requestId)
                    .build();
        } catch (InvocationTargetException | IllegalAccessException e) {
            responseDto = ResponseDto.builder()
                    .status(RowResponseStatus.INTERNAL_SERVER_ERROR.getId())
                    .requestId(requestId)
                    .build();
            log.catching(e);
        }

        try {
            String responsePayload = objectMapper.writeValueAsString(responseDto);
            webSocketSession.sendMessage(new TextMessage(responsePayload));
        } catch (IOException e) {
            log.catching(e);
        }
    }
}
