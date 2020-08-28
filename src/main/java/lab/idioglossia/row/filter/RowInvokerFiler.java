package lab.idioglossia.row.filter;

import lab.idioglossia.row.domain.RowResponseStatus;
import lab.idioglossia.row.domain.RowWebsocketSession;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.domain.protocol.ResponseDto;
import lab.idioglossia.row.exception.InvalidPathException;
import lab.idioglossia.row.service.RowInvokerService;

import java.lang.reflect.InvocationTargetException;

public class RowInvokerFiler implements RowFilter {
    private final RowInvokerService rowInvokerService;

    public RowInvokerFiler(RowInvokerService rowInvokerService) {
        this.rowInvokerService = rowInvokerService;
    }

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception {
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
