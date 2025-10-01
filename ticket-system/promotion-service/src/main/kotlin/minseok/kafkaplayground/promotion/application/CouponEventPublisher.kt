package minseok.kafkaplayground.promotion.application

import com.fasterxml.jackson.databind.ObjectMapper
import minseok.kafkaplayground.common.event.CouponLifecycleEvent
import minseok.kafkaplayground.common.support.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CouponEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(
        couponId: Long,
        policyId: Long,
        memberId: Long,
        status: String,
        occurredAt: Instant = Instant.now(),
    ) {
        val event =
            CouponLifecycleEvent(
                couponId = couponId,
                policyId = policyId,
                memberId = memberId,
                status = status,
                occurredAt = occurredAt,
            )
        val payload = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(KafkaTopics.COUPON_LIFECYCLE, couponId.toString(), payload)
    }
}
