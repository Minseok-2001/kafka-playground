package minseok.kafkaplayground.promotion.adapter.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

class CreateCouponPolicyRequest(
    @field:NotBlank
    val code: String,
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val benefitType: String,
    @field:NotNull
    val benefitValue: BigDecimal,
    val minimumAmount: BigDecimal?,
    @field:NotNull
    val validFrom: Instant,
    @field:Future
    val validUntil: Instant,
    val totalQuantity: Int?,
)
