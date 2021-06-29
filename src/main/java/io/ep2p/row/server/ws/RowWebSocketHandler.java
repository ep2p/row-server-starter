package io.ep2p.row.server.ws;

import io.ep2p.row.server.config.Naming;
import io.ep2p.row.server.config.properties.WebSocketProperties;
import io.ep2p.row.server.repository.RowSessionRegistry;
import io.ep2p.row.server.repository.SubscriptionRegistry;
import io.ep2p.row.server.service.ProtocolService;
import io.ep2p.row.server.utl.WebsocketSessionUtil;
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
    private final RowWsListener rowWsListener;
    private final SubscriptionRegistry subscriptionRegistry;
    private final ProtocolService protocolService;
    private final boolean trackHeartbeats;

    public RowWebSocketHandler(RowSessionRegistry rowSessionRegistry, WebSocketProperties webSocketProperties, RowWsListener rowWsListener, SubscriptionRegistry subscriptionRegistry, ProtocolService protocolService, boolean trackHeartbeats) {
        this.rowSessionRegistry = rowSessionRegistry;
        this.webSocketProperties = webSocketProperties;
        this.rowWsListener = rowWsListener;
        this.subscriptionRegistry = subscriptionRegistry;
        this.protocolService = protocolService;
        this.trackHeartbeats = trackHeartbeats;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = WebsocketSessionUtil.getUserId(session);
        Object extra = session.getAttributes().get(Naming.EXTRA_ATTRIBUTE_NAME);
        rowSessionRegistry.addSession(new SpringRowServerWebsocket(new ConcurrentWebSocketSessionDecorator(session, (int) webSocketProperties.getMaximumAsyncSendTimeout(), webSocketProperties.getMaxBinaryBuffer()), userId, extra));
        updateHeartbeat(session);
        rowWsListener.onOpen(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        protocolService.handle(rowSessionRegistry.getSession(WebsocketSessionUtil.getUserId(session), session.getId()), message);
        updateHeartbeat(session);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        updateHeartbeat(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        session.close();
        rowWsListener.onTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = WebsocketSessionUtil.getUserId(session);
        rowSessionRegistry.removeSession(userId, session.getId());
        rowWsListener.onConnectionClose(session, status);
        subscriptionRegistry.remove(userId, session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //--------------------

    public void updateHeartbeat(WebSocketSession session) {
        if(trackHeartbeats)
            session.getAttributes().put(Naming.IN_HEARTBEAT_ATTRIBUTE_NAME, new Date());
    }
}
