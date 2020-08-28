package lab.idioglossia.row.filter;

import lab.idioglossia.row.domain.RowWebsocketSession;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.domain.protocol.ResponseDto;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception;
}
