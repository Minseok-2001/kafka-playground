package minseok.kafkaplayground.common

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import minseok.kafkaplayground.common.support.TsidFactoryProvider
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity {
    @Id
    var id: Long = 0
        protected set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @LastModifiedDate
    var updatedAt: Instant = Instant.now()

    var deletedAt: Instant? = null

    @PrePersist
    protected fun assignId() {
        if (id == 0L) {
            id = TsidFactoryProvider.nextLong()
        }
    }

    fun markDeleted() {
        deletedAt = Instant.now()
    }
}
