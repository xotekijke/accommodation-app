package com.example.accommodation.service.impl;

import com.example.accommodation.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingNotificationService implements NotificationService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    public void sendNotification(String message) {
        LOGGER.info("Notification: {}", message);
    }
}
