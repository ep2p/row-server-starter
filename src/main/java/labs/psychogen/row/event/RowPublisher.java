package labs.psychogen.row.event;
import java.io.Serializable;

public interface RowPublisher<E extends Serializable> {
    void publish(E e);
}