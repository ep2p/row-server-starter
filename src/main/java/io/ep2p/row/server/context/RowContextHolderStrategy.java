package io.ep2p.row.server.context;

public interface RowContextHolderStrategy {
    void clearContext();
    RowContext getContext();
    void setContext(RowContext rowContext);
    RowContext createEmptyContext();
}
