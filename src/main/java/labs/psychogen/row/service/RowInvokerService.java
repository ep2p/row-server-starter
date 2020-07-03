package labs.psychogen.row.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.RowEndpoint;
import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.exception.InvalidPathException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RowInvokerService {
    private final EndpointProvider endpointProvider;
    private final PathMatcher pathMatcher;
    private final ObjectMapper objectMapper;

    public RowInvokerService(EndpointProvider endpointProvider) {
        this.endpointProvider = endpointProvider;
        pathMatcher = new AntPathMatcher();
        objectMapper = new ObjectMapper();
    }

    public Object invoke(RequestDto requestDto, ResponseDto responseDto) throws InvalidPathException, InvocationTargetException, IllegalAccessException, JsonProcessingException {
        RowEndpoint rowEndpoint = endpointProvider.getMatchingEndpoint(RowEndpoint.RowMethod.valueOf(requestDto.getMethod()), requestDto.getAddress());
        Object[] objects = new Object[rowEndpoint.getParametersCount()];
        fill(rowEndpoint, requestDto.getAddress(), objects, requestDto, responseDto);
        return rowEndpoint.getMethod().invoke(rowEndpoint.getBean(), objects);
    }

    private void fill(RowEndpoint rowEndpoint, String path, Object[] objects, RequestDto requestDto, ResponseDto responseDto) throws JsonProcessingException {
        Object body = getBody(rowEndpoint, requestDto);
        Object query = getQuery(rowEndpoint, requestDto);

        Map<String, String> map = pathMatcher.extractUriTemplateVariables(rowEndpoint.getFinalAddress(), path);
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

        if(rowEndpoint.getRequestIndex() != -1){
            objects[rowEndpoint.getRequestIndex()] = requestDto;
        }

        if(rowEndpoint.getResponseIndex() != -1){
            objects[rowEndpoint.getResponseIndex()] = responseDto;
        }
    }

    private Object getBody(RowEndpoint rowEndpoint, RequestDto requestDto) throws JsonProcessingException {
        if(requestDto.getBody() == null || requestDto.getBody().isNull() || rowEndpoint.getBody() == null)
            return null;
        return objectMapper.readValue(requestDto.getBody().toString(), rowEndpoint.getBody());
    }


    private Object getQuery(RowEndpoint rowEndpoint, RequestDto requestDto) throws JsonProcessingException {
        if(requestDto.getQuery() == null || requestDto.getQuery().isNull() || rowEndpoint.getQuery() == null)
            return null;
        return objectMapper.readValue(requestDto.getQuery().toString(), rowEndpoint.getQuery());
    }

}
