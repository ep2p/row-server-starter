package lab.idioglossia.row.context;

public interface RowContext {
    default boolean isRowRequest(){
        return false;
    }
    RowUser getRowUser();
    void setRowUser(RowUser rowUser);
}
