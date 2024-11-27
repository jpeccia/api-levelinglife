package com.jpeccia.levelinglife.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilita um broker de mensagens simples para enviar mensagens para clientes específicos
        registry.enableSimpleBroker("/queue", "/topic"); // Suporte a diferentes tipos de destinos
        registry.setApplicationDestinationPrefixes("/app"); // Prefixo usado para mensagens enviadas para o servidor
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") // Endpoint de WebSocket para a conexão STOMP
                .setAllowedOrigins("*") // Permite conexões de qualquer origem (para produção, é melhor restringir isso)
                .withSockJS(); // Suporte a fallback em caso de WebSocket não estar disponível
    }
}
