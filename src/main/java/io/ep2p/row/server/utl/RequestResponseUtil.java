package io.ep2p.row.server.utl;

import io.ep2p.row.server.config.Naming;
import io.ep2p.row.server.domain.protocol.RequestDto;
import io.ep2p.row.server.domain.protocol.ResponseDto;
import io.ep2p.row.server.event.Subscription;

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

    public static void addSubscriptionDetails(Subscription subscription, ResponseDto responseDto){
        addHeader(Naming.SUBSCRIPTION_EVENT_HEADER_NAME, subscription.event(), responseDto);
        addHeader(Naming.SUBSCRIPTION_Id_HEADER_NAME, subscription.id(), responseDto);
    }

    public static boolean isUnSubscribing(RequestDto requestDto){
        if(requestDto.getHeaders() != null && requestDto.getHeaders().containsKey(Naming.UNSUBSCRIBE_HEADER_NAME))
            return requestDto.getHeaders().get(Naming.UNSUBSCRIBE_HEADER_NAME).equals("1");

        return false;
    }
}
