package labs.psychogen.row.ws;

import labs.psychogen.row.domain.WebsocketUserData;
import labs.psychogen.row.exception.AuthenticationFailedException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static labs.psychogen.row.config.Naming.USER_ID_ATTRIBUTE_NAME;

public class RowHandshakeInterceptor implements HandshakeInterceptor {
    private final RowHandshakeAuthHandler rowHandshakeAuthHandler;
    private final TokenExtractor tokenExtractor;

    public RowHandshakeInterceptor(RowHandshakeAuthHandler rowHandshakeAuthHandler, TokenExtractor tokenExtractor) {
        this.rowHandshakeAuthHandler = rowHandshakeAuthHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        try {
            WebsocketUserData websocketUserData = rowHandshakeAuthHandler.handshake(tokenExtractor.getToken(serverHttpRequest));
            attributes.put(USER_ID_ATTRIBUTE_NAME, websocketUserData.getId());
            return true;
        } catch (AuthenticationFailedException e){
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
