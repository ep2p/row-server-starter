package lab.idioglossia.row.server.ws;


public interface NativeWebsocketSession {
    Object getNativeSession();
    <T> T getNativeSession(Class<T> var1);
}
