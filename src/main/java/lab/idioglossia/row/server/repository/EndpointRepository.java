package lab.idioglossia.row.server.repository;

import lab.idioglossia.row.server.domain.RowEndpoint;

import java.util.Collection;

public interface EndpointRepository {
    void addEndpoint(RowEndpoint rowEndpoint);
    void remove(RowEndpoint rowEndpoint);
    Collection<RowEndpoint> getAll();
}
