package labs.psychogen.row.ws;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

import java.util.List;

public interface TokenExtractor {
    String getToken(ServerHttpRequest serverHttpRequest);

    class RowTokenExtractor implements TokenExtractor {
        @Override
        public String getToken(ServerHttpRequest serverHttpRequest) {
            HttpHeaders handshakeHeaders = serverHttpRequest.getHeaders();
            if (handshakeHeaders.containsKey("Sec-WebSocket-Protocol")) {
                List<String> wecWebsocketProtocol = handshakeHeaders.get("Sec-WebSocket-Protocol");
                for (String parts : wecWebsocketProtocol) {
                    String[] split = parts.split(",");
                    for (String keyValue : split) {
                        if(keyValue.contains(":")){
                            String[] kvPair = keyValue.split(":");
                            if (kvPair.length == 2) {
                                if (kvPair[0].equals("token")) {
                                    return kvPair[1];
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
