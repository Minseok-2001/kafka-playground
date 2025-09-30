package minseok.kafkaplayground.notification.domain

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRequestRepository : JpaRepository<NotificationRequest, Long>
