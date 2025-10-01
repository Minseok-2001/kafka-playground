package minseok.kafkaplayground.promotion.application.command

import minseok.kafkaplayground.promotion.domain.BenefitType
import java.math.BigDecimal
import java.time.Instant

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
