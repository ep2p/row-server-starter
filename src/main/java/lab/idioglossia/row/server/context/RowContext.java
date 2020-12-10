package lab.idioglossia.row.server.context;

public interface RowContext {
    default boolean isRowRequest(){
        return false;
    }
    RowUser getRowUser();
    void setRowUser(RowUser rowUser);
}
