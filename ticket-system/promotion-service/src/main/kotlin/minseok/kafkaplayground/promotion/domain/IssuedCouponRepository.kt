package minseok.kafkaplayground.promotion.domain

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface IssuedCouponRepository : JpaRepository<IssuedCoupon, Long> {
    fun findByPolicyIdAndMemberId(policyId: Long, memberId: Long): Optional<IssuedCoupon>
}
