package lab.idioglossia.row.server.service;

import lab.idioglossia.row.server.domain.RowEndpoint;
import lab.idioglossia.row.server.exception.InvalidPathException;

public interface EndpointProvider {
    RowEndpoint getMatchingEndpoint(RowEndpoint.RowMethod method, String path) throws InvalidPathException;
}
