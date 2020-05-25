package labs.psychogen.row.config;

import labs.psychogen.row.properties.WebSocketProperties;
import labs.psychogen.row.ws.RowHandshakeInterceptor;
import labs.psychogen.row.ws.RowWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebsocketConfig implements WebSocketConfigurer {
    private final WebSocketProperties webSocketProperties;
    private final WebSocketHandler rowWebSocketHandler;
    private final RowHandshakeInterceptor rowHandshakeInterceptor;

    @Autowired
    public WebsocketConfig(WebSocketProperties webSocketProperties, RowWebSocketHandler rowWebSocketHandler, RowHandshakeInterceptor rowHandshakeInterceptor) {
        this.webSocketProperties = webSocketProperties;
        this.rowWebSocketHandler = rowWebSocketHandler;
        this.rowHandshakeInterceptor = rowHandshakeInterceptor;
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
        webSocketHandlerRegistry
            .addHandler(rowWebSocketHandler, webSocketProperties.getPrefix())
            .setAllowedOrigins(webSocketProperties.getAllowedOrigins())
            .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
            .addInterceptors(rowHandshakeInterceptor);
    }
}
