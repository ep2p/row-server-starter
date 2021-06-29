package io.ep2p.row.server.service;

import io.ep2p.row.server.exception.InvalidPathException;
import io.ep2p.row.server.domain.RowEndpoint;

public interface EndpointProvider {
    RowEndpoint getMatchingEndpoint(RowEndpoint.RowMethod method, String path) throws InvalidPathException;
}
