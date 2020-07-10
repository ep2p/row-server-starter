package labs.psychogen.row.config;

import labs.psychogen.row.event.EventPublisherProxy;
import labs.psychogen.row.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class PublisherProxyConfig {
    private final PublisherService publisherService;
    private final TaskExecutor publisherThreadExecutor;

    @Autowired
    public PublisherProxyConfig(PublisherService publisherService, TaskExecutor publisherThreadExecutor) {
        this.publisherService = publisherService;
        this.publisherThreadExecutor = publisherThreadExecutor;
    }

    @Bean
    public EventPublisherProxy publisherProxy() {
        return new EventPublisherProxy(publisherThreadExecutor, publisherService);
    }

    @Bean(name = "publisherProxyBeanFactory")
    public PublisherProxyBeanFactory webServiceProxyBeanFactory() {
        return new PublisherProxyBeanFactory();
    }
}
