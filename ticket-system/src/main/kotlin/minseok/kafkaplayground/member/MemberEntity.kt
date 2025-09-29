package minseok.kafkaplayground.member

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import minseok.kafkaplayground.common.BaseEntity


@Table
@Entity
class MemberEntity (
    var nickname : String,
): BaseEntity()



