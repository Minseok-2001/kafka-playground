package minseok.kafkaplayground.promotion.domain

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface CouponPolicyRepository : JpaRepository<CouponPolicy, Long> {
    fun findByCode(code: String): Optional<CouponPolicy>
}
