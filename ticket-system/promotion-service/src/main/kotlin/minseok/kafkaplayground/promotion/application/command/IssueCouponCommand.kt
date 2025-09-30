package minseok.kafkaplayground.promotion.application.command

data class IssueCouponCommand(
    val policyId: Long,
    val memberId: Long,
)
