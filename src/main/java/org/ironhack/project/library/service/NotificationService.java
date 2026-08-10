package org.ironhack.project.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async("notificationExecutor")
    public void sendWelcomeEmail(String username, String email) {
        simulateDelay();
        log.info("Welcome email sent -> user: {}, email: {}", username, email);
    }

    @Async("notificationExecutor")
    public void sendBorrowConfirmation(String memberName, String bookTitle) {
        simulateDelay();
        log.info("Book borrow notification sent -> member: {}, book: {}", memberName, bookTitle);
    }

    @Async("notificationExecutor")
    public void sendReturnConfirmation(String memberName, String bookTitle) {
        simulateDelay();
        log.info("Book return notification sent -> member: {}, book: {}", memberName, bookTitle);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}