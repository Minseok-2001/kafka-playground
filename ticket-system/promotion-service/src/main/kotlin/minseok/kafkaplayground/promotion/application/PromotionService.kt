package minseok.kafkaplayground.promotion.application

import minseok.kafkaplayground.promotion.application.command.CreateCouponPolicyCommand
import minseok.kafkaplayground.promotion.application.command.IssueCouponCommand
import minseok.kafkaplayground.promotion.application.command.RedeemCouponCommand
import minseok.kafkaplayground.promotion.domain.CouponPolicy
import minseok.kafkaplayground.promotion.domain.CouponPolicyRepository
import minseok.kafkaplayground.promotion.domain.CouponStatus
import minseok.kafkaplayground.promotion.domain.IssuedCoupon
import minseok.kafkaplayground.promotion.domain.IssuedCouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class PromotionService(
    private val couponPolicyRepository: CouponPolicyRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val couponEventPublisher: CouponEventPublisher,
) {
    @Transactional
    fun createPolicy(command: CreateCouponPolicyCommand): CouponPolicy {
        couponPolicyRepository.findByCode(command.code).ifPresent {
            throw IllegalArgumentException("duplicate coupon code: ${command.code}")
        }
        val policy =
            CouponPolicy(
                code = command.code,
                name = command.name,
                benefitType = command.benefitType,
                benefitValue = command.benefitValue,
                minimumAmount = command.minimumAmount,
                validFrom = command.validFrom,
                validUntil = command.validUntil,
                totalQuantity = command.totalQuantity,
            )
        return couponPolicyRepository.save(policy)
    }

    @Transactional
    fun issueCoupon(command: IssueCouponCommand): IssuedCoupon {
        val policy =
            couponPolicyRepository
                .findById(command.policyId)
                .orElseThrow { IllegalArgumentException("coupon policy not found: ${command.policyId}") }
        val now = Instant.now()
        if (!policy.canIssue(now)) {
            throw IllegalStateException("coupon policy unavailable for issue: ${command.policyId}")
        }
        val existing = issuedCouponRepository.findByPolicyIdAndMemberId(policy.id, command.memberId)
        if (existing.isPresent && existing.get().status == CouponStatus.AVAILABLE) {
            return existing.get()
        }

        val coupon =
            IssuedCoupon(
                policy = policy,
                memberId = command.memberId,
                expiresAt = policy.validUntil,
            )
        val saved = issuedCouponRepository.save(coupon)
        policy.increaseIssued()
        couponEventPublisher.publish(
            couponId = saved.id,
            policyId = policy.id,
            memberId = saved.memberId,
            status = "ISSUED",
            occurredAt = now,
        )
        return saved
    }

    @Transactional
    fun redeemCoupon(command: RedeemCouponCommand): IssuedCoupon {
        val coupon =
            issuedCouponRepository
                .findById(command.couponId)
                .orElseThrow { IllegalArgumentException("issued coupon not found: ${command.couponId}") }
        val now = Instant.now()
        coupon.redeem(now)
        val saved = issuedCouponRepository.save(coupon)
        couponEventPublisher.publish(
            couponId = saved.id,
            policyId = saved.policy.id,
            memberId = saved.memberId,
            status = "REDEEMED",
            occurredAt = now,
        )
        return saved
    }

    @Transactional(readOnly = true)
    fun findCoupon(couponId: Long): IssuedCoupon = issuedCouponRepository
        .findById(couponId)
        .orElseThrow { IllegalArgumentException("issued coupon not found: $couponId") }
}
