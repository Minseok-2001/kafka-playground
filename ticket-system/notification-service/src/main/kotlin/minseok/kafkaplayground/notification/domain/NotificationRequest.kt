package minseok.kafkaplayground.notification.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.Instant
import minseok.kafkaplayground.common.BaseEntity

@Entity
@Table(name = "notification_request")
class NotificationRequest(
    @Column(name = "member_id", nullable = false)
    val memberId: Long,
    @Column(name = "channel", nullable = false)
    val channel: String,
    @Column(name = "subject", nullable = false)
    var subject: String,
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    var body: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,
    @Column(name = "scheduled_at")
    var scheduledAt: Instant? = null,
    @Column(name = "sent_at")
    var sentAt: Instant? = null,
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    var failureReason: String? = null,
) : BaseEntity() {
    fun markSent(sendTime: Instant) {
        status = NotificationStatus.SENT
        sentAt = sendTime
        failureReason = null
    }

    fun markFailed(reason: String, failureTime: Instant) {
        status = NotificationStatus.FAILED
        failureReason = reason
        sentAt = failureTime
    }
}
