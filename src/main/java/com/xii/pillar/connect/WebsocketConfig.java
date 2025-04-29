package com.xii.pillar.connect;

import com.xii.pillar.service.endpoint.MessageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
    @Autowired
    private MessageReportService messageReportService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        CommonHandshakeInterceptor commonHandshakeInterceptor = commonHandshakeInterceptor();
        registry.addHandler(new PerConnectionWebSocketHandler(EndpointHandler.class, false),
                "/endpoint/connect").setAllowedOrigins("*").addInterceptors(commonHandshakeInterceptor);
    }

    @Bean
    public CommonHandshakeInterceptor commonHandshakeInterceptor() {
        return new CommonHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }

}
