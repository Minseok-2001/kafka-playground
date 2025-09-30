package minseok.kafkaplayground.promotion.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import minseok.kafkaplayground.common.BaseEntity

@Entity
@Table(name = "issued_coupon")
class IssuedCoupon(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    val policy: CouponPolicy,
    @Column(name = "member_id", nullable = false)
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CouponStatus = CouponStatus.AVAILABLE,
    @Column(name = "redeemed_at")
    var redeemedAt: Instant? = null,
    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,
) : BaseEntity() {
    fun redeem(now: Instant = Instant.now()) {
        if (status != CouponStatus.AVAILABLE) {
            throw IllegalStateException("coupon is not available: ")
        }
        if (now.isAfter(expiresAt)) {
            status = CouponStatus.EXPIRED
            throw IllegalStateException("coupon expired: ")
        }
        status = CouponStatus.REDEEMED
        redeemedAt = now
    }
}
