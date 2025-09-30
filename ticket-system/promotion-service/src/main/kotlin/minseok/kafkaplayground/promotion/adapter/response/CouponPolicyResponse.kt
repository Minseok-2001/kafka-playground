package minseok.kafkaplayground.promotion.adapter.response

import java.math.BigDecimal
import java.time.Instant

data class CouponPolicyResponse(
    val id: Long,
    val code: String,
    val name: String,
    val benefitType: String,
    val benefitValue: BigDecimal,
    val minimumAmount: BigDecimal?,
    val validFrom: Instant,
    val validUntil: Instant,
    val totalQuantity: Int?,
    val issuedQuantity: Int,
)
