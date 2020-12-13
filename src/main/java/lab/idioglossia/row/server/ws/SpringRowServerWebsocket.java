package lab.idioglossia.row.server.ws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

public class SpringRowServerWebsocket extends AbstractWebsocketSession<WebSocketSession> implements RowServerWebsocket<WebSocketSession> {
    private final WebSocketSession webSocketSession;
    @Getter
    @Setter
    private String userId;
    @Getter
    @Setter
    private Object extra;

    public SpringRowServerWebsocket(WebSocketSession webSocketSession) {
        super(webSocketSession.getAttributes());
        this.webSocketSession = webSocketSession;
    }

    public SpringRowServerWebsocket(WebSocketSession webSocketSession, String userId, Object extra) {
        this(webSocketSession);
        setUserId(userId);
        setExtra(extra);
    }

    public SpringRowServerWebsocket(WebSocketSession webSocketSession, Object extra) {
        this(webSocketSession);
        setExtra(extra);
    }

    public SpringRowServerWebsocket(WebSocketSession webSocketSession, String userId) {
        this(webSocketSession);
        setUserId(userId);
    }

    public Map<String, Object> getAttributes(){
        return webSocketSession.getAttributes();
    }

    @Override
    public String getId() {
        return webSocketSession.getId();
    }

    @Override
    public URI getUri() {
        return webSocketSession.getUri();
    }

    @Override
    public void close(CloseStatus closeStatus) throws IOException {
        webSocketSession.close(closeStatus);
    }

    @Override
    public boolean isOpen() {
        return webSocketSession.isOpen();
    }

    @Override
    public boolean isSecure() {
        return Objects.requireNonNull(webSocketSession.getUri()).toString().contains("wss://");
    }

    @Override
    public void sendTextMessage(String payload) throws IOException {
        webSocketSession.sendMessage(new TextMessage(payload));
    }

    @Override
    public void sendPingMessage(ByteBuffer byteBuffer) throws IOException {
        webSocketSession.sendMessage(new PingMessage(byteBuffer));
    }

    @Override
    public void sendPongMessage(ByteBuffer byteBuffer) throws IOException {
        webSocketSession.sendMessage(new PongMessage(byteBuffer));
    }

    @Override
    public void closeInternal(CloseStatus closeStatus) throws IOException {
        webSocketSession.close(closeStatus);
    }
}
