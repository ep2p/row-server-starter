package lab.idioglossia.row.ws;

import lab.idioglossia.row.exception.AuthenticationFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

import java.util.List;
import java.util.UUID;

public interface TokenExtractor {
    String getToken(ServerHttpRequest serverHttpRequest) throws AuthenticationFailedException;

    class NoTokenExtractor implements TokenExtractor {
        @Override
        public String getToken(ServerHttpRequest serverHttpRequest) throws AuthenticationFailedException {
            System.out.println("getToken() is called");
            return UUID.randomUUID().toString();
        }
    }

    class SecWebsocketProtocolTokenExtractor implements TokenExtractor {
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
