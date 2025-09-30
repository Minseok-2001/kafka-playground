package minseok.kafkaplayground.ticket.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import minseok.kafkaplayground.common.BaseEntity

@Entity
@Table(name = "ticket_reservations")
class TicketReservation(
    @Column(name = "member_id", nullable = false)
    var memberId: Long,
    @Column(name = "seat_number", nullable = false)
    var seatNumber: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReservationStatus = ReservationStatus.PENDING,
) : BaseEntity() {
    fun confirm() {
        status = ReservationStatus.CONFIRMED
    }

    fun compensate() {
        status = ReservationStatus.COMPENSATED
    }

    fun cancel() {
        status = ReservationStatus.CANCELLED
    }
}

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    COMPENSATED,
    CANCELLED,
}
