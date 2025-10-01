package minseok.kafkaplayground.promotion.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface IssuedCouponRepository : JpaRepository<IssuedCoupon, Long> {
    fun findByPolicyIdAndMemberId(
        policyId: Long,
        memberId: Long,
    ): Optional<IssuedCoupon>
}
