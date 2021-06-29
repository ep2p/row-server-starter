package io.ep2p.row.server.config;

import io.ep2p.row.server.config.properties.WebSocketProperties;
import io.ep2p.row.server.ws.RowHandshakeTokenInterceptor;
import io.ep2p.row.server.ws.RowProtocolHandshakeInterceptor;
import io.ep2p.row.server.ws.RowWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnClass(RowConfiguration.class)
@DependsOn({"rowHandshakeTokenInterceptor", "rowWebSocketHandler"})
@ConditionalOnProperty(value = "row.ws.enable", havingValue = "true")
public class WebsocketConfiguration implements WebSocketConfigurer {
    private final WebSocketProperties webSocketProperties;
    private final WebSocketHandler rowWebSocketHandler;
    private final RowHandshakeTokenInterceptor rowHandshakeTokenInterceptor;

    @Autowired
    public WebsocketConfiguration(WebSocketProperties webSocketProperties, RowWebSocketHandler rowWebSocketHandler, RowHandshakeTokenInterceptor rowHandshakeTokenInterceptor) {
        this.webSocketProperties = webSocketProperties;
        this.rowWebSocketHandler = rowWebSocketHandler;
        this.rowHandshakeTokenInterceptor = rowHandshakeTokenInterceptor;
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
            .addInterceptors(new RowProtocolHandshakeInterceptor(), rowHandshakeTokenInterceptor);
    }
}
