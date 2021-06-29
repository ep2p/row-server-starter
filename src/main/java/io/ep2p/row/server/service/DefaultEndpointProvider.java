package io.ep2p.row.server.service;

import io.ep2p.row.server.exception.InvalidPathException;
import io.ep2p.row.server.repository.EndpointRepository;
import io.ep2p.row.server.domain.RowEndpoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class DefaultEndpointProvider implements EndpointProvider {
    private final EndpointRepository endpointRepository;
    private final PathMatcher pathMatcher;

    public DefaultEndpointProvider(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;
        pathMatcher = new AntPathMatcher();
    }

    @Override
    public RowEndpoint getMatchingEndpoint(RowEndpoint.RowMethod method, String path) throws InvalidPathException {
        for (RowEndpoint rowEndpoint : endpointRepository.getAll()) {
            if (!rowEndpoint.getRowMethod().equals(method)) {
                continue;
            }
            for (String address : rowEndpoint.getAddresses()) {
                if (pathMatcher.match(rowEndpoint.getPrefix() + "" + address, path)) {
                    rowEndpoint.setFinalAddress(rowEndpoint.getPrefix() + "" + address); //limitation
                    return rowEndpoint;
                }
            }
        }
        throw new InvalidPathException(path, method);
    }
}
