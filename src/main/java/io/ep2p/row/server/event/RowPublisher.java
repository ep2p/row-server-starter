package io.ep2p.row.server.event;
import java.io.Serializable;

public interface RowPublisher<E extends Serializable> {
    void publish(E e);
}