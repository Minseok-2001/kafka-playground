package minseok.kafkaplayground.notification.application

import minseok.kafkaplayground.notification.application.command.CreateNotificationCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationFailedCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationSentCommand
import minseok.kafkaplayground.notification.domain.NotificationRequest
import minseok.kafkaplayground.notification.domain.NotificationRequestRepository
import minseok.kafkaplayground.notification.domain.NotificationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class NotificationService(
    private val notificationRequestRepository: NotificationRequestRepository,
    private val notificationEventPublisher: NotificationEventPublisher,
) {
    @Transactional
    fun create(command: CreateNotificationCommand): NotificationRequest {
        val now = Instant.now()
        val notification =
            NotificationRequest(
                memberId = command.memberId,
                channel = command.channel,
                subject = command.subject,
                body = command.body,
                scheduledAt = command.scheduledAt,
            )
        val saved = notificationRequestRepository.save(notification)
        val shouldDispatch = command.scheduledAt?.isBefore(now) != false
        if (shouldDispatch) {
            dispatch(saved, now)
        }
        return saved
    }

    @Transactional
    fun markSent(command: MarkNotificationSentCommand): NotificationRequest {
        val notification = load(command.notificationId)
        val now = Instant.now()
        dispatch(notification, now)
        return notification
    }

    @Transactional
    fun markFailed(command: MarkNotificationFailedCommand): NotificationRequest {
        val notification = load(command.notificationId)
        val now = Instant.now()
        notification.markFailed(command.reason, now)
        notificationRequestRepository.save(notification)
        notificationEventPublisher.publish(
            notificationId = notification.id,
            memberId = notification.memberId,
            channel = notification.channel,
            status = NotificationStatus.FAILED.name,
            occurredAt = now,
        )
        return notification
    }

    @Transactional(readOnly = true)
    fun get(notificationId: Long): NotificationRequest = load(notificationId)

    private fun dispatch(
        notification: NotificationRequest,
        sendTime: Instant,
    ) {
        notification.markSent(sendTime)
        notificationRequestRepository.save(notification)
        notificationEventPublisher.publish(
            notificationId = notification.id,
            memberId = notification.memberId,
            channel = notification.channel,
            status = NotificationStatus.SENT.name,
            occurredAt = sendTime,
        )
    }

    private fun load(notificationId: Long): NotificationRequest = notificationRequestRepository
        .findById(notificationId)
        .orElseThrow { IllegalArgumentException("notification not found: $notificationId") }
}
