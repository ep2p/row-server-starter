package lab.idioglossia.row.server.ws;

import java.util.Map;

public interface RowServerWebsocket<S> extends NativeWebsocketSession, WebsocketSession {
    Map<String, Object> getAttributes();
}
