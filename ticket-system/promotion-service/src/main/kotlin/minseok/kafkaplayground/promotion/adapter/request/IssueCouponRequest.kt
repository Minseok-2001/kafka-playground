package minseok.kafkaplayground.promotion.adapter.request

import jakarta.validation.constraints.NotNull

class IssueCouponRequest(
    @field:NotNull
    val memberId: Long,
)
