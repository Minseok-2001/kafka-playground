package minseok.kafkaplayground.promotion.adapter.response

import minseok.kafkaplayground.promotion.domain.CouponPolicy
import minseok.kafkaplayground.promotion.domain.IssuedCoupon

fun CouponPolicy.toResponse(): CouponPolicyResponse = CouponPolicyResponse(
    id = id,
    code = code,
    name = name,
    benefitType = benefitType.name,
    benefitValue = benefitValue,
    minimumAmount = minimumAmount,
    validFrom = validFrom,
    validUntil = validUntil,
    totalQuantity = totalQuantity,
    issuedQuantity = issuedQuantity,
)

fun IssuedCoupon.toResponse(): IssuedCouponResponse = IssuedCouponResponse(
    id = id,
    policyId = policy.id,
    memberId = memberId,
    status = status.name,
    redeemedAt = redeemedAt,
    expiresAt = expiresAt,
)
