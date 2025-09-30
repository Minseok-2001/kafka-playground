package minseok.kafkaplayground.promotion.application

import io.mockk.Called
import io.mockk.MockKMatcherScope
import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import minseok.kafkaplayground.promotion.application.command.CreateCouponPolicyCommand
import minseok.kafkaplayground.promotion.application.command.IssueCouponCommand
import minseok.kafkaplayground.promotion.application.command.RedeemCouponCommand
import minseok.kafkaplayground.promotion.domain.BenefitType
import minseok.kafkaplayground.promotion.domain.CouponPolicy
import minseok.kafkaplayground.promotion.domain.CouponPolicyRepository
import minseok.kafkaplayground.promotion.domain.CouponStatus
import minseok.kafkaplayground.promotion.domain.IssuedCoupon
import minseok.kafkaplayground.promotion.domain.IssuedCouponRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PromotionServiceTest {
    private val couponPolicyRepository = mockk<CouponPolicyRepository>()
    private val issuedCouponRepository = mockk<IssuedCouponRepository>()
    private val couponEventPublisher = mockk<CouponEventPublisher>(relaxed = true)
    private val clock = Clock.fixed(Instant.parse("2025-10-01T00:00:00Z"), ZoneOffset.UTC)
    private lateinit var promotionService: PromotionService

    @BeforeEach
    fun setUp() {
        promotionService = PromotionService(
            couponPolicyRepository,
            issuedCouponRepository,
            couponEventPublisher,
            clock,
        )
    }

    @Test
    fun `should create coupon policy`() {
        val command = CreateCouponPolicyCommand(
            code = "WELCOME10",
            name = "Welcome coupon",
            benefitType = BenefitType.AMOUNT,
            benefitValue = BigDecimal.TEN,
            minimumAmount = BigDecimal.valueOf(50),
            validFrom = Instant.parse("2025-09-30T00:00:00Z"),
            validUntil = Instant.parse("2025-10-30T00:00:00Z"),
            totalQuantity = 100,
        )
        val policy = CouponPolicy(
            code = command.code,
            name = command.name,
            benefitType = command.benefitType,
            benefitValue = command.benefitValue,
            minimumAmount = command.minimumAmount,
            validFrom = command.validFrom,
            validUntil = command.validUntil,
            totalQuantity = command.totalQuantity,
        )
        setId(policy, 10)

        val savedPolicy = slot<CouponPolicy>()
        given { couponPolicyRepository.findByCode(command.code) } returns Optional.empty()
        given { couponPolicyRepository.save(capture(savedPolicy)) } returns policy

        val result = promotionService.createPolicy(command)

        assertEquals(10, result.id)
        assertEquals("WELCOME10", result.code)
    }

    @Test
    fun `should prevent duplicated coupon code`() {
        val command = CreateCouponPolicyCommand(
            code = "WELCOME10",
            name = "dup",
            benefitType = BenefitType.AMOUNT,
            benefitValue = BigDecimal.TEN,
            minimumAmount = null,
            validFrom = Instant.now(clock),
            validUntil = Instant.now(clock).plusSeconds(3600),
            totalQuantity = null,
        )
        val existing = CouponPolicy(
            code = command.code,
            name = command.name,
            benefitType = command.benefitType,
            benefitValue = command.benefitValue,
            minimumAmount = command.minimumAmount,
            validFrom = command.validFrom,
            validUntil = command.validUntil,
            totalQuantity = command.totalQuantity,
        )
        setId(existing, 1)

        given { couponPolicyRepository.findByCode(command.code) } returns Optional.of(existing)

        assertFailsWith<IllegalArgumentException> {
            promotionService.createPolicy(command)
        }
        verify { couponEventPublisher wasNot Called }
    }

    @Test
    fun `should issue coupon and publish event`() {
        val policy = CouponPolicy(
            code = "WELCOME10",
            name = "Welcome",
            benefitType = BenefitType.AMOUNT,
            benefitValue = BigDecimal.TEN,
            minimumAmount = null,
            validFrom = Instant.parse("2025-09-30T00:00:00Z"),
            validUntil = Instant.parse("2025-11-01T00:00:00Z"),
            totalQuantity = 2,
        )
        setId(policy, 5)
        val command = IssueCouponCommand(policyId = policy.id, memberId = 55)
        val savedCoupon = IssuedCoupon(
            policy = policy,
            memberId = command.memberId,
            expiresAt = policy.validUntil,
        )
        setId(savedCoupon, 99)

        val issuedSlot = slot<IssuedCoupon>()
        given { couponPolicyRepository.findById(policy.id) } returns Optional.of(policy)
        given { issuedCouponRepository.findByPolicyIdAndMemberId(policy.id, command.memberId) } returns Optional.empty()
        given { issuedCouponRepository.save(capture(issuedSlot)) } returns savedCoupon

        val result = promotionService.issueCoupon(command)

        assertEquals(99, result.id)
        assertEquals(CouponStatus.AVAILABLE, result.status)
        assertEquals(1, policy.issuedQuantity)
        val publishedAt = slot<Instant>()
        then { couponEventPublisher.publish(99, 5, 55, "ISSUED", capture(publishedAt)) }
    }

    @Test
    fun `should redeem coupon and emit event`() {
        val policy = CouponPolicy(
            code = "WELCOME10",
            name = "Welcome",
            benefitType = BenefitType.AMOUNT,
            benefitValue = BigDecimal.TEN,
            minimumAmount = null,
            validFrom = Instant.parse("2025-09-30T00:00:00Z"),
            validUntil = Instant.parse("2025-11-01T00:00:00Z"),
            totalQuantity = 10,
        )
        setId(policy, 5)
        val coupon = IssuedCoupon(
            policy = policy,
            memberId = 99,
            expiresAt = policy.validUntil,
        )
        setId(coupon, 501)

        given { issuedCouponRepository.findById(coupon.id) } returns Optional.of(coupon)
        given { issuedCouponRepository.save(coupon) } returns coupon

        val result = promotionService.redeemCoupon(RedeemCouponCommand(couponId = coupon.id))

        assertEquals(CouponStatus.REDEEMED, result.status)
        val publishedAt = slot<Instant>()
        then { couponEventPublisher.publish(501, 5, 99, "REDEEMED", capture(publishedAt)) }
    }

    private fun setId(entity: Any, value: Long) {
        val field = entity.javaClass.superclass!!.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(entity, value)
    }
}

private fun <T> given(block: MockKMatcherScope.() -> T) = every(block)

private fun then(block: MockKVerificationScope.() -> Unit) = verify(verifyBlock = block)
