package labs.psychogen.row.ws;

import labs.psychogen.row.config.Naming;
import labs.psychogen.row.domain.RowWebsocketSession;
import labs.psychogen.row.filter.RowFilterChain;
import labs.psychogen.row.properties.WebSocketProperties;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.service.ProtocolService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;

public class RowWebSocketHandler extends TextWebSocketHandler {
    private final RowSessionRegistry rowSessionRegistry;
    private final WebSocketProperties webSocketProperties;
    private final ProtocolService protocolService;

    public RowWebSocketHandler(RowSessionRegistry rowSessionRegistry, WebSocketProperties webSocketProperties, RowFilterChain rowFilterChain) {
        this.rowSessionRegistry = rowSessionRegistry;
        this.webSocketProperties = webSocketProperties;
        protocolService = new ProtocolService(rowFilterChain);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get(Naming.USER_ID_ATTRIBUTE_NAME);
        rowSessionRegistry.addSession(RowWebsocketSession.builder()
                .session(new ConcurrentWebSocketSessionDecorator(session, (int) webSocketProperties.getMaximumAsyncSendTimeout(), webSocketProperties.getMaxBinaryBuffer()))
                .userId(userId)

                .build());
        updateHeartbeat(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        updateHeartbeat(session);
        protocolService.handle(session, message);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        updateHeartbeat(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    //--------------------

    private void updateHeartbeat(WebSocketSession session) {
        session.getAttributes().put(Naming.IN_HEARTBEAT_ATTRIBUTE_NAME, new Date());
    }
}
