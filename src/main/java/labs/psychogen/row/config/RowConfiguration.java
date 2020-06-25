package labs.psychogen.row.config;

import labs.psychogen.row.filter.RowFilter;
import labs.psychogen.row.filter.RowFilterChain;
import labs.psychogen.row.filter.RowInvokerFiler;
import labs.psychogen.row.properties.HandlerProperties;
import labs.psychogen.row.properties.RowProperties;
import labs.psychogen.row.properties.WebSocketProperties;
import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SetEndpointRepository;
import labs.psychogen.row.repository.SubscriptionRegistry;
import labs.psychogen.row.service.*;
import labs.psychogen.row.ws.RowHandshakeAuthHandler;
import labs.psychogen.row.ws.RowHandshakeTokenInterceptor;
import labs.psychogen.row.ws.RowWebSocketHandler;
import labs.psychogen.row.ws.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties({WebSocketProperties.class, HandlerProperties.class, RowProperties.class})
@ConditionalOnProperty(value = "row.enable", havingValue = "true")
@Import({WebsocketConfig.class, RowApplicationListener.class})
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
    @DependsOn("rowInvokerFilter")
    public RowFilterChain rowFilterChain(RowFilter rowInvokerFilter){
        List<RowFilter> rowFilters = new CopyOnWriteArrayList<>();
        rowFilters.add(rowInvokerFilter);
        return new RowFilterChain(rowFilters);
    }

    @Bean("filterScanner")
    @DependsOn("rowFilterChain")
    public FilterScanner filterScanner(RowFilterChain rowFilterChain){
        return new FilterScanner(rowFilterChain);
    }

    @Bean("rowWebSocketHandler")
    @DependsOn({"sessionRegistry", "rowFilterChain"})
    public RowWebSocketHandler rowWebSocketHandler(RowSessionRegistry sessionRegistry, RowFilterChain rowFilterChain){
        return new RowWebSocketHandler(sessionRegistry, webSocketProperties, rowFilterChain);
    }

    @Bean("subscriptionRegistry")
    public SubscriptionRegistry subscriptionRegistry(){
        return new SubscriptionRegistry.RowSubscriptionRegistry();
    }

    @Bean("subscriberService")
    @DependsOn("subscriptionRegistry")
    public SubscriberService subscriberService(SubscriptionRegistry subscriptionRegistry){
        return new SubscriberService(subscriptionRegistry);
    }

    @Bean("publisherService")
    @DependsOn({"subscriptionRegistry", "rowSessionRegistry"})
    public PublisherService publisherService(SubscriptionRegistry subscriptionRegistry, RowSessionRegistry rowSessionRegistry){
        return new PublisherService(subscriptionRegistry, rowSessionRegistry);
    }

}
