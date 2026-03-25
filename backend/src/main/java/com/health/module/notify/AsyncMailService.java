package com.health.module.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步邮件通知；未配置 spring.mail.host 时无 JavaMailSender Bean，仅记录日志
 */
@Slf4j
@Service
public class AsyncMailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendAlertEmail(String to, String subject, String text) {
        if (mailSender == null) {
            log.debug("未配置邮件服务，跳过发送预警邮件至 {}", to);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("预警邮件已发送至 {}", to);
        } catch (Exception e) {
            log.warn("预警邮件发送失败: {}", e.getMessage());
        }
    }
}
