package lab.idioglossia.row.server.filter;

import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.ws.RowServerWebsocket;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, RowServerWebsocket<?> rowServerWebsocket) throws Exception;
}
