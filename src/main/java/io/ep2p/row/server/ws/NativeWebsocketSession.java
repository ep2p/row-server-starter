package io.ep2p.row.server.ws;


public interface NativeWebsocketSession {
    Object getNativeSession();
    <T> T getNativeSession(Class<T> var1);
}
