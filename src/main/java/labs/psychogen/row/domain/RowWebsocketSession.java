package labs.psychogen.row.domain;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RowWebsocketSession {
    private volatile WebSocketSession session;
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RowWebsocketSession)) return false;
        RowWebsocketSession that = (RowWebsocketSession) o;
        return getSession().getId().equals(that.getSession().getId()) &&
                getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSession(), getUserId());
    }
}
