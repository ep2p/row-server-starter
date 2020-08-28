package lab.idioglossia.row.service;

import lab.idioglossia.row.RowEndpoint;
import lab.idioglossia.row.exception.InvalidPathException;

public interface EndpointProvider {
    RowEndpoint getMatchingEndpoint(RowEndpoint.RowMethod method, String path) throws InvalidPathException;
}
