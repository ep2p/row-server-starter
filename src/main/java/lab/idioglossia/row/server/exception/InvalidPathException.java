package lab.idioglossia.row.server.exception;

import lab.idioglossia.row.server.domain.RowEndpoint;
import lombok.Getter;

@Getter
public class InvalidPathException extends Exception {
    private final String path;
    private final RowEndpoint.RowMethod rowMethod;

    public InvalidPathException(String path, RowEndpoint.RowMethod rowMethod) {
        this.path = path;
        this.rowMethod = rowMethod;
    }

    public InvalidPathException(String message, String path, RowEndpoint.RowMethod rowMethod) {
        super(message);
        this.path = path;
        this.rowMethod = rowMethod;
    }

    public InvalidPathException(String message, Throwable cause, String path, RowEndpoint.RowMethod rowMethod) {
        super(message, cause);
        this.path = path;
        this.rowMethod = rowMethod;
    }

    public InvalidPathException(Throwable cause, String path, RowEndpoint.RowMethod rowMethod) {
        super(cause);
        this.path = path;
        this.rowMethod = rowMethod;
    }

    public InvalidPathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String path, RowEndpoint.RowMethod rowMethod) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.path = path;
        this.rowMethod = rowMethod;
    }
}
