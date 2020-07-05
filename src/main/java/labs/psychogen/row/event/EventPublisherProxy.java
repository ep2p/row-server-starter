package labs.psychogen.row.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.AbstractInvocationHandler;
import labs.psychogen.row.annotations.Publish;
import labs.psychogen.row.domain.protocol.ResponseDto;
import labs.psychogen.row.service.PublisherService;
import labs.psychogen.row.utl.RequestResponseUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

import static labs.psychogen.row.config.Naming.SUBSCRIPTION_EVENT_HEADER_NAME;

public class EventPublisherProxy extends AbstractInvocationHandler {
    private final ObjectMapper objectMapper;
    private final TaskExecutor publisherTaskExecutor;
    private final PublisherService publisherService;

    public EventPublisherProxy(ObjectMapper objectMapper, TaskExecutor publisherTaskExecutor, PublisherService publisherService) {
        this.objectMapper = objectMapper;
        this.publisherTaskExecutor = publisherTaskExecutor;
        this.publisherService = publisherService;
    }

    @Override
    protected Object handleInvocation(Object o, Method method, Object[] args) throws Throwable {
        //Skip proxy when other methods are called
        if(!method.getName().equals("publish")){
            return method.invoke(o, args);
        }

        Object message = (args.length > 0) ? args[0] : null;
        Assert.notNull(message, "Message can not be null");

        Publish publish = method.getDeclaringClass().getAnnotation(Publish.class);
        String json = objectMapper.writeValueAsString(getResponse(message, publish.value()));
        if(publish.async()){
            publisherTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    publisherService.publish(publish.value(), json);
                }
            });
        }else {
            publisherService.publish(publish.value(), json);
        }

        return Void.TYPE;
    }

    private ResponseDto getResponse(Object message, String event) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setBody(message);
        RequestResponseUtil.addHeader(SUBSCRIPTION_EVENT_HEADER_NAME, event, responseDto);
        return responseDto;
    }
}
