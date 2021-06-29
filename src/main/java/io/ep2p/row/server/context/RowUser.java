package io.ep2p.row.server.context;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RowUser {
    private String userId;
    private String sessionId;
}
