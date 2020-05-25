package labs.psychogen.row.domain;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RowWebsocketSession {
    private volatile WebSocketSession session;
    private String userId;
}
