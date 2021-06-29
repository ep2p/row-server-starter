package io.ep2p.row.server.domain.protocol;

import io.ep2p.row.server.domain.RowResponseStatus;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResponseDto {
    private String requestId;
    private Object body;
    private int status;
    private Map<String, String> headers;
    @Builder.Default
    private String type = "response";

    public void setStatus(RowResponseStatus status) {
        this.status = status.getId();
    }
}
