package minseok.kafkaplayground.promotion.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import minseok.kafkaplayground.common.BaseEntity
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "coupon_policy")
class CouponPolicy(
    @Column(name = "code", nullable = false, unique = true)
    val code: String,
    @Column(name = "name", nullable = false)
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    val benefitType: BenefitType,
    @Column(name = "benefit_value", nullable = false)
    val benefitValue: BigDecimal,
    @Column(name = "minimum_amount")
    val minimumAmount: BigDecimal?,
    @Column(name = "valid_from", nullable = false)
    val validFrom: Instant,
    @Column(name = "valid_until", nullable = false)
    val validUntil: Instant,
    @Column(name = "total_quantity")
    val totalQuantity: Int?,
    @Column(name = "issued_quantity", nullable = false)
    var issuedQuantity: Int = 0,
) : BaseEntity() {
    fun canIssue(now: Instant): Boolean {
        val withinPeriod = now.isAfter(validFrom) || now == validFrom
        val beforeExpiry = now.isBefore(validUntil)
        val hasStock = totalQuantity?.let { issuedQuantity < it } ?: true
        return withinPeriod && beforeExpiry && hasStock
    }

    fun increaseIssued() {
        issuedQuantity += 1
    }
}
