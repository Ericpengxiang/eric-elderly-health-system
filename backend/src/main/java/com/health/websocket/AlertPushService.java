package com.health.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 通过 STOMP 广播预警到订阅 /topic/alerts 的前端
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertPushService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(AlertMessage message) {
        try {
            messagingTemplate.convertAndSend("/topic/alerts", message);
        } catch (Exception e) {
            log.warn("预警 WebSocket 推送失败: {}", e.getMessage());
        }
    }
}
