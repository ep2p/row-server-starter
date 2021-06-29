package io.ep2p.row.server.ws;

import io.ep2p.row.server.config.Naming;
import io.ep2p.row.server.domain.WebsocketUserData;
import io.ep2p.row.server.exception.AuthenticationFailedException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class RowHandshakeTokenInterceptor implements HandshakeInterceptor {
    private final RowHandshakeAuthHandler rowHandshakeAuthHandler;
    private final TokenExtractor tokenExtractor;

    public RowHandshakeTokenInterceptor(RowHandshakeAuthHandler rowHandshakeAuthHandler, TokenExtractor tokenExtractor) {
        this.rowHandshakeAuthHandler = rowHandshakeAuthHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        try {
            WebsocketUserData websocketUserData = rowHandshakeAuthHandler.handshake(tokenExtractor.getToken(serverHttpRequest));
            attributes.put(Naming.USER_ID_ATTRIBUTE_NAME, websocketUserData.getId());
            if(websocketUserData.getExtra() != null)
                attributes.put(Naming.EXTRA_ATTRIBUTE_NAME, websocketUserData.getExtra());
            return true;
        } catch (AuthenticationFailedException e){
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
