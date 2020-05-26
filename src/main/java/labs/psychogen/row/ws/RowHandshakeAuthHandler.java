package labs.psychogen.row.ws;

import labs.psychogen.row.domain.WebsocketUserData;
import labs.psychogen.row.exception.AuthenticationFailedException;
import org.springframework.lang.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public interface RowHandshakeAuthHandler {
    WebsocketUserData handshake(@Nullable String token) throws AuthenticationFailedException;

    class DefaultRowHandshakeAuthHandler implements RowHandshakeAuthHandler {
        private volatile AtomicLong atomicLong = new AtomicLong(0);

        @Override
        public WebsocketUserData handshake(String token) {
            return new WebsocketUserData(String.valueOf(atomicLong.getAndIncrement()));
        }
    }
}
