package io.ep2p.row.server.context;

public class DefaultContextImpl implements RowContext {
    private volatile RowUser rowUser;
    private final boolean rowRequest;
    private static final long serialVersionUID = 100L;

    public DefaultContextImpl(RowUser rowUser, boolean isRowRequest) {
        this.rowUser = rowUser;
        this.rowRequest = isRowRequest;
    }

    @Override
    public boolean isRowRequest() {
        return this.rowRequest;
    }

    @Override
    public RowUser getRowUser() {
        return rowUser;
    }

    @Override
    public void setRowUser(RowUser rowUser) {
        this.rowUser = rowUser;
    }
}
