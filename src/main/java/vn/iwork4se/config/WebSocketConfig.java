package vn.iwork4se.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("[WEBSOCKET] Configuring message broker");
        config.enableSimpleBroker("/topic", "/queue");
//                .setHeartbeatValue(new long[]{25000, 25000});
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("[WEBSOCKET] Registering STOMP endpoints");
        registry.addEndpoint("/ws-message")
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-notification")
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");

        log.info("[WEBSOCKET] STOMP endpoints registered: /ws-message, /ws-notification");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(500 * 1024) // 500KB
                .setSendBufferSizeLimit(500 * 1024)
                .setSendTimeLimit(20000);
    }
}
