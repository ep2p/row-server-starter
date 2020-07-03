package labs.psychogen.row.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.event.EventPublisherProxy;
import labs.psychogen.row.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class PublisherProxyConfig {
    private final PublisherService publisherService;
    private final ObjectMapper objectMapper;
    private final TaskExecutor publisherThreadExecutor;

    @Autowired
    public PublisherProxyConfig(PublisherService publisherService, ObjectMapper objectMapper, TaskExecutor publisherThreadExecutor) {
        this.publisherService = publisherService;
        this.objectMapper = objectMapper;
        this.publisherThreadExecutor = publisherThreadExecutor;
    }

    @Bean
    public EventPublisherProxy publisherProxy() {
        return new EventPublisherProxy(objectMapper, publisherThreadExecutor, publisherService);
    }

    @Bean(name = "publisherProxyBeanFactory")
    public PublisherProxyBeanFactory webServiceProxyBeanFactory() {
        return new PublisherProxyBeanFactory();
    }
}
