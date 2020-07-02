package labs.psychogen.row.filter;

import labs.psychogen.row.domain.RowResponseStatus;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.exception.InvalidPathException;
import labs.psychogen.row.service.RowInvokerService;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;

public class RowInvokerFiler implements RowFilter {
    private final RowInvokerService rowInvokerService;

    public RowInvokerFiler(RowInvokerService rowInvokerService) {
        this.rowInvokerService = rowInvokerService;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, WebSocketSession webSocketSession) throws Exception {
        try {
            Object invoke = rowInvokerService.invoke(requestDto);
            responseDto.setBody(invoke);
        }catch (InvalidPathException e) {
            responseDto.setStatus(RowResponseStatus.NOT_FOUND);
        } catch (InvocationTargetException | IllegalAccessException e) {
            responseDto.setStatus(RowResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return true;
    }
}
