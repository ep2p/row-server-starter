package lab.idioglossia.row.server.ws;


import java.util.Map;

public abstract class AbstractWebsocketSession<T> implements NativeWebsocketSession, WebsocketSession {
    private T nativeSession;
    private final Map<String, Object> attributes;

    public AbstractWebsocketSession(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public T getNativeSession() {
        assert this.nativeSession != null;
        return this.nativeSession;
    }

    public <R> R getNativeSession(Class<R> requiredType) {
        return requiredType != null && !requiredType.isInstance(this.nativeSession) ? null : (R) this.nativeSession;
    }

    public void setNativeSession(T nativeSession) {
        this.nativeSession = nativeSession;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
