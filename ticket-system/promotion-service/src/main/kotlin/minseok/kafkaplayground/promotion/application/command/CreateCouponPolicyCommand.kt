package minseok.kafkaplayground.promotion.application.command

import java.math.BigDecimal
import java.time.Instant
import minseok.kafkaplayground.promotion.domain.BenefitType

data class CreateCouponPolicyCommand(
    val code: String,
    val name: String,
    val benefitType: BenefitType,
    val benefitValue: BigDecimal,
    val minimumAmount: BigDecimal?,
    val validFrom: Instant,
    val validUntil: Instant,
    val totalQuantity: Int?,
)
