package labs.psychogen.row.filter;

import labs.psychogen.row.domain.RowWebsocketSession;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception;
}
