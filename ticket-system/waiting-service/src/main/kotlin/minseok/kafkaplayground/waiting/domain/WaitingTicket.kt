package minseok.kafkaplayground.waiting.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import minseok.kafkaplayground.common.BaseEntity
import java.time.Instant

@Entity
@Table(name = "waiting_ticket")
class WaitingTicket(
    @Column(name = "queue_code", nullable = false)
    val queueCode: String,
    @Column(name = "member_id", nullable = false)
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: WaitingStatus = WaitingStatus.WAITING,
    @Column(name = "issued_sequence", nullable = false)
    val issuedSequence: Long,
    @Column(name = "expired_at", nullable = false)
    var expiredAt: Instant,
) : BaseEntity() {
    fun markAdmitted() {
        status = WaitingStatus.ADMITTED
    }

    fun markExpired() {
        status = WaitingStatus.EXPIRED
    }

    fun isExpired(now: Instant): Boolean = now.isAfter(expiredAt)
}
