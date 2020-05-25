package labs.psychogen.row.config;

import labs.psychogen.row.properties.WebSocketProperties;
import labs.psychogen.row.repository.EndpointRepository;
import labs.psychogen.row.repository.RowSessionRegistry;
import labs.psychogen.row.repository.SetEndpointRepository;
import labs.psychogen.row.service.DefaultEndpointProvider;
import labs.psychogen.row.service.EndpointProvider;
import labs.psychogen.row.ws.RowHandshakeAuthHandler;
import labs.psychogen.row.ws.RowHandshakeInterceptor;
import labs.psychogen.row.ws.RowWebSocketHandler;
import labs.psychogen.row.ws.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
@ComponentScan("labs.psychogen.row")
@EnableConfigurationProperties(WebSocketProperties.class)
public class RowConfiguration {
    private final ApplicationContext applicationContext;
    private final WebSocketProperties webSocketProperties;

    @Autowired
    public RowConfiguration(ApplicationContext applicationContext, WebSocketProperties webSocketProperties) {
        this.applicationContext = applicationContext;
        this.webSocketProperties = webSocketProperties;
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
        return new RowSessionRegistry.DefaultRowSessionRegistry();
    }

    @Bean("rowWebSocketHandler")
    @DependsOn({"sessionRegistry"})
    public RowWebSocketHandler rowWebSocketHandler(RowSessionRegistry sessionRegistry){
        return new RowWebSocketHandler(sessionRegistry, webSocketProperties);
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
