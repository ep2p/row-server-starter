package labs.psychogen.row.repository;

import labs.psychogen.row.RowEndpoint;

import java.util.Collection;

public interface EndpointRepository {
    void addEndpoint(RowEndpoint rowEndpoint);
    void remove(RowEndpoint rowEndpoint);
    Collection<RowEndpoint> getAll();
}
