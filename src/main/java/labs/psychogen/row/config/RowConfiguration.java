package labs.psychogen.row.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import labs.psychogen.row.filter.*;
import labs.psychogen.row.config.properties.HandlerProperties;
import labs.psychogen.row.config.properties.RowProperties;
import labs.psychogen.row.config.properties.WebSocketProperties;
import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SetEndpointRepository;
import labs.psychogen.row.repository.SubscriptionRegistry;
import labs.psychogen.row.service.*;
import labs.psychogen.row.ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties({WebSocketProperties.class, HandlerProperties.class, RowProperties.class})
@ConditionalOnProperty(value = "row.enable", havingValue = "true")
@Import({WebsocketConfig.class, RowApplicationListener.class, PublisherProxyConfig.class, PublisherProxyBeansRegistrar.class, PublisherProxyConfig.class})
public class RowConfiguration {
    private final WebSocketProperties webSocketProperties;
    private final HandlerProperties handlerProperties;

    @Autowired
    public RowConfiguration(WebSocketProperties webSocketProperties, HandlerProperties handlerProperties) {
        this.webSocketProperties = webSocketProperties;
        this.handlerProperties = handlerProperties;
    }

    @Bean
    @ConditionalOnMissingBean(EndpointRepository.class)
    public EndpointRepository endpointRepository(){
        return new SetEndpointRepository();
    }

    @Bean
    @ConditionalOnMissingBean({RowScanner.class})
    @DependsOn({"endpointRepository"})
    public RowScanner rowScanner(EndpointRepository endpointRepository){
        return new RowScanner(endpointRepository);
    }

    @Bean
    @ConditionalOnMissingBean({EndpointProvider.class})
    @DependsOn({"endpointRepository"})
    public EndpointProvider endpointProvider(EndpointRepository endpointRepository){
        return new DefaultEndpointProvider(endpointRepository);
    }

    @Bean
    @ConditionalOnMissingBean({RowSessionRegistry.class})
    public RowSessionRegistry sessionRegistry(){
        if(handlerProperties.isAllowMultipleSessionsForSingleUser()){
            return new RowSessionRegistry.MultiUserSessionRegistry();
        }
        return new RowSessionRegistry.DefaultRowSessionRegistry();
    }

    @Bean("rowInvokerService")
    @DependsOn({"endpointProvider"})
    public RowInvokerService rowInvokerService(EndpointProvider endpointProvider){
        return new RowInvokerService(endpointProvider);
    }

    @Bean
    @ConditionalOnMissingBean({RowHandshakeAuthHandler.class})
    public RowHandshakeAuthHandler rowHandshakeAuthHandler(){
        return new RowHandshakeAuthHandler.DefaultRowHandshakeAuthHandler();
    }

    @Bean
    @ConditionalOnMissingBean({TokenExtractor.class})
    public TokenExtractor tokenExtractor(){
        return new TokenExtractor.SecWebsocketProtocolTokenExtractor();
    }

    @Bean("rowHandshakeTokenInterceptor")
    @DependsOn({"rowHandshakeAuthHandler", "tokenExtractor"})
    public HandshakeInterceptor rowHandshakeInterceptor(RowHandshakeAuthHandler rowHandshakeAuthHandler, TokenExtractor tokenExtractor){
        return new RowHandshakeTokenInterceptor(rowHandshakeAuthHandler, tokenExtractor);
    }

    @Bean("rowInvokerFilter")
    @DependsOn("rowInvokerService")
    public RowFilter rowInvokerFilter(RowInvokerService rowInvokerService){
        return new RowInvokerFiler(rowInvokerService);
    }

    @Bean("rowFilterChain")
    @DependsOn({"rowInvokerFilter", "subscriberService"})
    public RowFilterChain rowFilterChain(RowFilter rowInvokerFilter, SubscriberService subscriberService){
        List<RowFilter> rowFilters = new CopyOnWriteArrayList<>();
        rowFilters.add(new SubscribeFilter(subscriberService, true));
        rowFilters.add(new UnSubscribeFilter(subscriberService));
        rowFilters.add(rowInvokerFilter);
        rowFilters.add(new SubscribeFilter(subscriberService, false));
        return new RowFilterChain(rowFilters);
    }

    @Bean("filterScanner")
    @DependsOn("rowFilterChain")
    public FilterScanner filterScanner(RowFilterChain rowFilterChain){
        return new FilterScanner(rowFilterChain);
    }

    @Bean("rowWsListener")
    @ConditionalOnMissingBean(RowWsListener.class)
    public RowWsListener rowWsListener(){
        return new RowWsListener.DummyRowWsListener();
    }


    @Bean("rowWebSocketHandler")
    @DependsOn({"sessionRegistry", "rowFilterChain", "rowWsListener", "subscriptionRegistry"})
    public RowWebSocketHandler rowWebSocketHandler(RowSessionRegistry sessionRegistry, RowFilterChain rowFilterChain, RowWsListener rowWsListener, SubscriptionRegistry subscriptionRegistry){
        return new RowWebSocketHandler(sessionRegistry, webSocketProperties, rowFilterChain, rowWsListener, subscriptionRegistry, handlerProperties.isTrackHeartbeats());
    }

    @Bean("subscriptionRegistry")
    public SubscriptionRegistry subscriptionRegistry(){
        return new SubscriptionRegistry.RowSubscriptionRegistry();
    }

    @Bean("subscriberService")
    @DependsOn({"subscriptionRegistry", "endpointProvider"})
    public SubscriberService subscriberService(SubscriptionRegistry subscriptionRegistry, EndpointProvider endpointProvider){
        return new SubscriberService(subscriptionRegistry, endpointProvider);
    }

    @Bean("publisherService")
    @DependsOn({"subscriptionRegistry", "rowSessionRegistry"})
    public PublisherService publisherService(SubscriptionRegistry subscriptionRegistry, RowSessionRegistry rowSessionRegistry){
        return new PublisherService(subscriptionRegistry, rowSessionRegistry);
    }

    @Bean("rowTaskExecutor")
    public TaskExecutor rowTaskExecutor(RowProperties rowProperties){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setQueueCapacity(rowProperties.getPublisherQueue());
        taskExecutor.setMaxPoolSize(rowProperties.getPublisherMaxPoolSize());
        taskExecutor.setAllowCoreThreadTimeOut(false);
        taskExecutor.setCorePoolSize(rowProperties.getPublisherCorePoolSize());
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setBeanName("rowTaskExecutor");
        return taskExecutor;
    }

    @Bean("objectMapper")
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
