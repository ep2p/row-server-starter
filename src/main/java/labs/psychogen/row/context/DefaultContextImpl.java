package labs.psychogen.row.context;

public class DefaultContextImpl implements RowContext {
    private volatile RowUser rowUser;
    private static final long serialVersionUID = 100L;

    public DefaultContextImpl(RowUser rowUser) {
        this.rowUser = rowUser;
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
