package labs.psychogen.row.service;

import labs.psychogen.row.RowEndpoint;
import labs.psychogen.row.exception.InvalidPathException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RowInvokerService {
    private final EndpointProvider endpointProvider;
    private final PathMatcher pathMatcher;

    public RowInvokerService(EndpointProvider endpointProvider) {
        this.endpointProvider = endpointProvider;
        pathMatcher = new AntPathMatcher();
    }

    public Object invoke(RowEndpoint.RowMethod rowMethod, String path, Object body, Object query) throws InvalidPathException, InvocationTargetException, IllegalAccessException {
        RowEndpoint rowEndpoint = endpointProvider.getMatchingEndpoint(rowMethod, path);
        Object[] objects = new Object[rowEndpoint.getParametersCount()];
        fill(rowEndpoint, path, objects, body, query);
        return rowEndpoint.getMethod().invoke(rowEndpoint.getBean(), objects);
    }

    private void fill(RowEndpoint rowEndpoint, String path, Object[] objects, Object body, Object query) {
        Map<String, String> map = pathMatcher.extractUriTemplateVariables(rowEndpoint.getPrefix() + rowEndpoint.getFinalAddress(), path);
        map.keySet().forEach(key -> {
            Integer integer = rowEndpoint.getPathVariables().get(key);
            objects[integer] = map.get(key);
        });

        if(rowEndpoint.getBodyIndex() != -1){
            objects[rowEndpoint.getBodyIndex()] = body;
        }

        if(rowEndpoint.getQueryIndex() != -1){
            objects[rowEndpoint.getQueryIndex()] = query;
        }
    }

}
