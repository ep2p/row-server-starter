package lab.idioglossia.row.server.event;

import com.google.common.reflect.AbstractInvocationHandler;
import lab.idioglossia.row.server.annotations.Publish;
import lab.idioglossia.row.server.service.PublisherService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class EventPublisherProxy extends AbstractInvocationHandler {
    private final TaskExecutor publisherTaskExecutor;
    private final PublisherService publisherService;

    public EventPublisherProxy(TaskExecutor publisherTaskExecutor, PublisherService publisherService) {
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
        if(publish.async()){
            publisherTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    publisherService.publish(publish.value(), message);
                }
            });
        }else {
            publisherService.publish(publish.value(), message);
        }

        return Void.TYPE;
    }


}
