package lab.idioglossia.row.server.filter;

import lab.idioglossia.row.server.domain.RowWebsocketSession;
import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception;
}
