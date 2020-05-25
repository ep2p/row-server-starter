package labs.psychogen.row.config;

import labs.psychogen.row.properties.HandlerProperties;
import labs.psychogen.row.properties.WebSocketProperties;
import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SetEndpointRepository;
import labs.psychogen.row.service.DefaultEndpointProvider;
import labs.psychogen.row.service.EndpointProvider;
import labs.psychogen.row.service.RowInvokerService;
import labs.psychogen.row.ws.RowHandshakeAuthHandler;
import labs.psychogen.row.ws.RowHandshakeInterceptor;
import labs.psychogen.row.ws.RowWebSocketHandler;
import labs.psychogen.row.ws.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties({WebSocketProperties.class, HandlerProperties.class})
@ConditionalOnProperty(value = "psychogen.row", havingValue = "true")
@Import(WebsocketConfig.class)
public class RowConfiguration {
    private final ApplicationContext applicationContext;
    private final WebSocketProperties webSocketProperties;
    private final HandlerProperties handlerProperties;

    @Autowired
    public RowConfiguration(ApplicationContext applicationContext, WebSocketProperties webSocketProperties, HandlerProperties handlerProperties) {
        this.applicationContext = applicationContext;
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
        return new RowScanner(applicationContext, endpointRepository);
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

    @Bean("rowWebSocketHandler")
    @DependsOn({"sessionRegistry", "rowInvokerService"})
    public RowWebSocketHandler rowWebSocketHandler(RowSessionRegistry sessionRegistry, RowInvokerService rowInvokerService){
        return new RowWebSocketHandler(sessionRegistry, webSocketProperties, rowInvokerService);
    }

    @Bean
    @ConditionalOnMissingBean({RowHandshakeAuthHandler.class})
    public RowHandshakeAuthHandler rowHandshakeAuthHandler(){
        return new RowHandshakeAuthHandler.DefaultHandshakeListener();
    }

    @Bean
    @ConditionalOnMissingBean({TokenExtractor.class})
    public TokenExtractor tokenExtractor(){
        return new TokenExtractor.RowTokenExtractor();
    }

    @Bean
    @DependsOn({"rowHandshakeListener", "tokenExtractor"})
    public HandshakeInterceptor rowHandshakeInterceptor(RowHandshakeAuthHandler rowHandshakeListener, TokenExtractor tokenExtractor){
        return new RowHandshakeInterceptor(rowHandshakeListener, tokenExtractor);
    }


}
