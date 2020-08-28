package lab.idioglossia.row.repository;

import lab.idioglossia.row.RowEndpoint;

import java.util.Collection;

public interface EndpointRepository {
    void addEndpoint(RowEndpoint rowEndpoint);
    void remove(RowEndpoint rowEndpoint);
    Collection<RowEndpoint> getAll();
}
