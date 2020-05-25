package labs.psychogen.row.domain.protocol;

import labs.psychogen.row.domain.RowResponseStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto {
    private String requestId;
    private Object body;
    private int status;

    public void setStatus(RowResponseStatus status) {
        this.status = status.getId();
    }
}
