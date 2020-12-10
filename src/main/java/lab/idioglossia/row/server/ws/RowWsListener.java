package lab.idioglossia.row.server.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface RowWsListener {
    void onOpen(WebSocketSession webSocketSession);
    void onTransportError(WebSocketSession webSocketSession, Throwable exception);
    void onConnectionClose(WebSocketSession webSocketSession, CloseStatus status);

    class DummyRowWsListener implements RowWsListener {
        @Override
        public void onOpen(WebSocketSession webSocketSession) {

        }

        @Override
        public void onTransportError(WebSocketSession webSocketSession, Throwable exception) {

        }

        @Override
        public void onConnectionClose(WebSocketSession webSocketSession, CloseStatus status) {

        }
    }
}
