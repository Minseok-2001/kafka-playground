package minseok.kafkaplayground.waiting.support

import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisWaitingQueueStore(
    private val stringRedisTemplate: StringRedisTemplate,
) : WaitingQueueStore {
    private val defaultTtl = Duration.ofHours(6)

    override fun enqueue(queueCode: String, ticketId: Long, sequence: Long) {
        val key = queueKey(queueCode)
        stringRedisTemplate.opsForZSet().add(key, ticketId.toString(), sequence.toDouble())
        stringRedisTemplate.expire(key, defaultTtl)
    }

    override fun remove(queueCode: String, ticketId: Long) {
        stringRedisTemplate.opsForZSet().remove(queueKey(queueCode), ticketId.toString())
    }

    override fun position(queueCode: String, ticketId: Long): Long? {
        val rank = stringRedisTemplate.opsForZSet().rank(queueKey(queueCode), ticketId.toString())
        return rank?.plus(1)
    }

    private fun queueKey(queueCode: String): String = "waiting:"
}
