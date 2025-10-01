package minseok.kafkaplayground.member.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MemberAccountRepository : JpaRepository<MemberAccount, Long> {
    fun findByEmail(email: String): Optional<MemberAccount>
}
