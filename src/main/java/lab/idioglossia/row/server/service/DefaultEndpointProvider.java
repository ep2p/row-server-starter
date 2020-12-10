package lab.idioglossia.row.server.service;

import lab.idioglossia.row.server.domain.RowEndpoint;
import lab.idioglossia.row.server.exception.InvalidPathException;
import lab.idioglossia.row.server.repository.EndpointRepository;
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
