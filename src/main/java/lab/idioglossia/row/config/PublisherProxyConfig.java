package lab.idioglossia.row.config;

import lab.idioglossia.row.event.EventPublisherProxy;
import lab.idioglossia.row.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class PublisherProxyConfig {
    private final PublisherService publisherService;
    private final TaskExecutor rowTaskExecutor;

    @Autowired
    public PublisherProxyConfig(PublisherService publisherService, TaskExecutor rowTaskExecutor) {
        this.publisherService = publisherService;
        this.rowTaskExecutor = rowTaskExecutor;
    }

    @Bean
    public EventPublisherProxy publisherProxy() {
        return new EventPublisherProxy(rowTaskExecutor, publisherService);
    }

    @Bean(name = "publisherProxyBeanFactory")
    public PublisherProxyBeanFactory webServiceProxyBeanFactory() {
        return new PublisherProxyBeanFactory();
    }
}