package lab.idioglossia.row.server.ws;

import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

public interface WebsocketSession {
    String getId();
    URI getUri();
    default String getUserId(){
        return "unknown";
    }
    void close(CloseStatus closeStatus) throws IOException;
    boolean isOpen();
    boolean isSecure();
    void sendTextMessage(String payload) throws IOException;
    void sendPingMessage(ByteBuffer byteBuffer) throws IOException;
    void sendPongMessage(ByteBuffer byteBuffer) throws IOException;
    void closeInternal(CloseStatus closeStatus) throws IOException;
}
