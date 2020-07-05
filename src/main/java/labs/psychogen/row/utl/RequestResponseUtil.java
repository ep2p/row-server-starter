package labs.psychogen.row.utl;

import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;

import java.util.HashMap;

public class RequestResponseUtil {
    public static void addHeader(String name, String value, ResponseDto responseDto){
        if (responseDto.getHeader() == null) {
            responseDto.setHeader(new HashMap<>());
        }

        responseDto.getHeader().put(name, value);
    }

    public static String getHeaderValue(String headerName, RequestDto requestDto){
        return requestDto.getHeader() == null ? null : requestDto.getHeader().get(headerName);
    }
}
