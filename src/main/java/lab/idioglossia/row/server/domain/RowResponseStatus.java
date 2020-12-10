package lab.idioglossia.row.server.domain;

import lombok.Getter;

@Getter
public enum RowResponseStatus {
    OK(200), UNAUTHORIZED(403), INTERNAL_SERVER_ERROR(500), NOT_FOUND(404), PROTOCOL_ERROR(-1), OTHER(400);
    private final int id;

    RowResponseStatus(int id) {
        this.id = id;
    }
}
