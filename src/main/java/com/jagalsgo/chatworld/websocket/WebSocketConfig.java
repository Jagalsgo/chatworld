package com.jagalsgo.chatworld.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws 엔드포인트 등록, SockJS 사용
        registry.addEndpoint("/chatWebsocket").setAllowedOriginPatterns("*").withSockJS();
    }

    // 메시지 브로커 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 도착지점
        registry.setApplicationDestinationPrefixes("/app");
        // 구독한 유저에게 전달
        registry.enableSimpleBroker("/topic");
    }

}
