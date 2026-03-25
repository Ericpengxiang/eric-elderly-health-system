package com.health.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 推送的预警消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {

    private Long alertId;
    private Long elderUserId;
    private String alertType;
    private String alertLevel;
    private String content;
    private long timestamp;
}
