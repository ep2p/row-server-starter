package io.ep2p.row.server.ws;

import io.ep2p.row.server.domain.WebsocketUserData;
import io.ep2p.row.server.exception.AuthenticationFailedException;
import org.springframework.lang.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public interface RowHandshakeAuthHandler {
    WebsocketUserData handshake(@Nullable String token) throws AuthenticationFailedException;

    class DefaultRowHandshakeAuthHandler implements RowHandshakeAuthHandler {
        private volatile AtomicLong atomicLong = new AtomicLong(0);

        @Override
        public WebsocketUserData handshake(String token) {
            return new WebsocketUserData(String.valueOf(atomicLong.getAndIncrement()), null);
        }
    }
}
