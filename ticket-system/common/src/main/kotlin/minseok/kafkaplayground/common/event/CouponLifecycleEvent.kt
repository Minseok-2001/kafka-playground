package minseok.kafkaplayground.common.event

import java.time.Instant

data class CouponLifecycleEvent(
    val couponId: Long,
    val policyId: Long,
    val memberId: Long,
    val status: String,
    val occurredAt: Instant,
)
