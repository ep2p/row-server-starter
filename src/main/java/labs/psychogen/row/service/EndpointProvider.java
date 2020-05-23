package labs.psychogen.row.service;

import labs.psychogen.row.RowEndpoint;
import labs.psychogen.row.exception.InvalidPathException;

public interface EndpointProvider {
    RowEndpoint getMatchingEndpoint(RowEndpoint.RowMethod method, String path) throws InvalidPathException;
}
