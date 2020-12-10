package lab.idioglossia.row.server.event;
import java.io.Serializable;

public interface RowPublisher<E extends Serializable> {
    void publish(E e);
}