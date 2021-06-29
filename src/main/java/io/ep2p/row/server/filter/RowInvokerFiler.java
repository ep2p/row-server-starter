package io.ep2p.row.server.filter;

import io.ep2p.row.server.exception.InvalidPathException;
import io.ep2p.row.server.service.RowInvokerService;
import io.ep2p.row.server.domain.RowResponseStatus;
import io.ep2p.row.server.domain.protocol.RequestDto;
import io.ep2p.row.server.domain.protocol.ResponseDto;
import io.ep2p.row.server.ws.RowServerWebsocket;

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
