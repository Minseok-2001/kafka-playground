package minseok.kafkaplayground.payment.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import minseok.kafkaplayground.common.BaseEntity

@Entity
@Table(name = "payment_transaction")
class PaymentTransaction(
    @Column(name = "reservation_id", nullable = false)
    val reservationId: Long,
    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.REQUESTED,
) : BaseEntity() {
    fun approve() {
        status = PaymentStatus.APPROVED
    }

    fun reject() {
        status = PaymentStatus.REJECTED
    }

    fun compensate() {
        status = PaymentStatus.COMPENSATED
    }
}
