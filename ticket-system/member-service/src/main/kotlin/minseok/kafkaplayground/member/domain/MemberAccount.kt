package minseok.kafkaplayground.member.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import minseok.kafkaplayground.common.BaseEntity

@Entity
@Table(name = "member_account")
class MemberAccount(
    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    @Column(name = "nickname", nullable = false)
    var nickname: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: MemberStatus = MemberStatus.ACTIVE,
    @Column(name = "notification_channel")
    var notificationChannel: String? = null,
) : BaseEntity() {
    fun updateProfile(newNickname: String, newNotificationChannel: String?) {
        nickname = newNickname
        notificationChannel = newNotificationChannel
    }

    fun suspend() {
        status = MemberStatus.SUSPENDED
    }

    fun activate() {
        status = MemberStatus.ACTIVE
    }

    fun withdraw() {
        status = MemberStatus.WITHDRAWN
    }
}
