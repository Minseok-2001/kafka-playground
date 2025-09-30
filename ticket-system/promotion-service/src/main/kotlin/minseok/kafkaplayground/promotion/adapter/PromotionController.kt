package minseok.kafkaplayground.promotion.adapter

import jakarta.validation.Valid
import minseok.kafkaplayground.promotion.adapter.request.CreateCouponPolicyRequest
import minseok.kafkaplayground.promotion.adapter.request.IssueCouponRequest
import minseok.kafkaplayground.promotion.adapter.response.CouponPolicyResponse
import minseok.kafkaplayground.promotion.adapter.response.IssuedCouponResponse
import minseok.kafkaplayground.promotion.adapter.response.toResponse
import minseok.kafkaplayground.promotion.application.PromotionService
import minseok.kafkaplayground.promotion.application.command.CreateCouponPolicyCommand
import minseok.kafkaplayground.promotion.application.command.IssueCouponCommand
import minseok.kafkaplayground.promotion.application.command.RedeemCouponCommand
import minseok.kafkaplayground.promotion.domain.BenefitType
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/promotions")
@Validated
class PromotionController(
    private val promotionService: PromotionService,
) {
    @PostMapping("/policies")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPolicy(@Valid @RequestBody request: CreateCouponPolicyRequest): CouponPolicyResponse {
        val command = CreateCouponPolicyCommand(
            code = request.code,
            name = request.name,
            benefitType = BenefitType.valueOf(request.benefitType.uppercase()),
            benefitValue = request.benefitValue,
            minimumAmount = request.minimumAmount,
            validFrom = request.validFrom,
            validUntil = request.validUntil,
            totalQuantity = request.totalQuantity,
        )
        return promotionService.createPolicy(command).toResponse()
    }

    @PostMapping("/policies/{policyId}/issue")
    @ResponseStatus(HttpStatus.CREATED)
    fun issueCoupon(
        @PathVariable policyId: Long,
        @Valid @RequestBody request: IssueCouponRequest,
    ): IssuedCouponResponse {
        val command = IssueCouponCommand(
            policyId = policyId,
            memberId = request.memberId,
        )
        return promotionService.issueCoupon(command).toResponse()
    }

    @PostMapping("/coupons/{couponId}/redeem")
    fun redeemCoupon(@PathVariable couponId: Long): IssuedCouponResponse {
        val command = RedeemCouponCommand(couponId = couponId)
        return promotionService.redeemCoupon(command).toResponse()
    }

    @GetMapping("/coupons/{couponId}")
    fun getCoupon(@PathVariable couponId: Long): IssuedCouponResponse {
        return promotionService.findCoupon(couponId).toResponse()
    }
}
