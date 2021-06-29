package io.ep2p.row.server.repository;

import io.ep2p.row.server.domain.RowEndpoint;

import java.util.Collection;

public interface EndpointRepository {
    void addEndpoint(RowEndpoint rowEndpoint);
    void remove(RowEndpoint rowEndpoint);
    Collection<RowEndpoint> getAll();
}
