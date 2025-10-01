package minseok.kafkaplayground.promotion.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CouponPolicyRepository : JpaRepository<CouponPolicy, Long> {
    fun findByCode(code: String): Optional<CouponPolicy>
}
