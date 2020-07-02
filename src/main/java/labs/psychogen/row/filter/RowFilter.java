package labs.psychogen.row.filter;

import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import org.springframework.web.socket.WebSocketSession;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto, WebSocketSession webSocketSession) throws Exception;
}
