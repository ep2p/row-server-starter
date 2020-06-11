package labs.psychogen.row.context;

public interface RowContextHolderStrategy {
    void clearContext();
    RowContext getContext();
    void setContext(RowContext rowContext);
    RowContext createEmptyContext();
}
