package labs.psychogen.row.filter;

import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;

public interface RowFilter {
    boolean filter(RequestDto requestDto, ResponseDto responseDto) throws Exception;
}
