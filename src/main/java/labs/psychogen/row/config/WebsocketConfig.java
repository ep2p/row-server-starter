package labs.psychogen.row.config;

import labs.psychogen.row.properties.WebSocketProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebsocketConfig implements WebSocketConfigurer {
    private final WebSocketProperties webSocketProperties;

    public WebsocketConfig(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
    }

    @Bean
    @ConditionalOnMissingBean(WebSocketContainerFactoryBean.class)
    public WebSocketContainerFactoryBean createWebSocketContainer() {
        WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
        container.setAsyncSendTimeout(webSocketProperties.getMaximumAsyncSendTimeout());
        container.setMaxBinaryMessageBufferSize(webSocketProperties.getMaxBinaryBuffer());
        container.setMaxSessionIdleTimeout(webSocketProperties.getMaximumSessionIdle());
        container.setMaxTextMessageBufferSize(webSocketProperties.getMaxTextBuffer());
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //todo
    }
}
