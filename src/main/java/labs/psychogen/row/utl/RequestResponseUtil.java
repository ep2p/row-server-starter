package labs.psychogen.row.utl;

import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;

import java.util.HashMap;

public class RequestResponseUtil {
    public static void addHeader(String name, String value, ResponseDto responseDto){
        if (responseDto.getHeaders() == null) {
            responseDto.setHeaders(new HashMap<>());
        }

        responseDto.getHeaders().put(name, value);
    }

    public static String getHeaderValue(String headerName, RequestDto requestDto){
        return requestDto.getHeaders() == null ? null : requestDto.getHeaders().get(headerName);
    }
}
