package minseok.kafkaplayground.member.domain

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface MemberAccountRepository : JpaRepository<MemberAccount, Long> {
    fun findByEmail(email: String): Optional<MemberAccount>
}
