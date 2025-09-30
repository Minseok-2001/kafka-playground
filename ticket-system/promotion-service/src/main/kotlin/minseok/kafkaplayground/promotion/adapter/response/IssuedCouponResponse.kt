package minseok.kafkaplayground.promotion.adapter.response

import java.time.Instant

data class IssuedCouponResponse(
    val id: Long,
    val policyId: Long,
    val memberId: Long,
    val status: String,
    val redeemedAt: Instant?,
    val expiresAt: Instant,
)
