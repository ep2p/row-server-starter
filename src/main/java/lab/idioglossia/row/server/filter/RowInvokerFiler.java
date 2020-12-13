package lab.idioglossia.row.server.filter;

import lab.idioglossia.row.server.domain.RowResponseStatus;
import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.exception.InvalidPathException;
import lab.idioglossia.row.server.service.RowInvokerService;
import lab.idioglossia.row.server.ws.RowServerWebsocket;

import java.lang.reflect.InvocationTargetException;

public class RowInvokerFiler implements RowFilter {
    private final RowInvokerService rowInvokerService;

    public RowInvokerFiler(RowInvokerService rowInvokerService) {
        this.rowInvokerService = rowInvokerService;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowServerWebsocket<?> rowServerWebsocket) throws Exception {
        try {
            Object invoke = rowInvokerService.invoke(requestDto, responseDto);
            responseDto.setBody(invoke);
        }catch (InvalidPathException e) {
            responseDto.setStatus(RowResponseStatus.NOT_FOUND);
        } catch (InvocationTargetException | IllegalAccessException e) {
            responseDto.setStatus(RowResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return true;
    }
}
