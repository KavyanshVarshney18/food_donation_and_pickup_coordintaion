package com.fooddonation.food_donation_system.websocket;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publish(String eventType, Object data) {
        Map<String, Object> payload = Map.of("event", eventType, "data", data);
        messagingTemplate.convertAndSend("/topic/donations", (Object) payload);
    }
}
