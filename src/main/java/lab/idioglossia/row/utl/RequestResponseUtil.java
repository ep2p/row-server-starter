package lab.idioglossia.row.utl;

import lab.idioglossia.row.config.Naming;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.domain.protocol.ResponseDto;
import lab.idioglossia.row.event.Subscription;

import java.util.HashMap;

import static lab.idioglossia.row.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;
import static lab.idioglossia.row.config.Naming.SUBSCRIPTION_Id_HEADER_NAME;

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
        addHeader(SUBSCRIPTION_EVENT_HEADER_NAME, subscription.event(), responseDto);
        addHeader(SUBSCRIPTION_Id_HEADER_NAME, subscription.id(), responseDto);
    }

    public static boolean isUnSubscribing(RequestDto requestDto){
        if(requestDto.getHeaders() != null && requestDto.getHeaders().containsKey(Naming.UNSUBSCRIBE_HEADER_NAME))
            return requestDto.getHeaders().get(Naming.UNSUBSCRIBE_HEADER_NAME).equals("1");

        return false;
    }
}
