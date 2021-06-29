package io.ep2p.row.server.filter;

import io.ep2p.row.server.domain.protocol.RequestDto;
import io.ep2p.row.server.domain.protocol.ResponseDto;
import io.ep2p.row.server.ws.RowServerWebsocket;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, RowServerWebsocket<?> rowServerWebsocket) throws Exception;
}
