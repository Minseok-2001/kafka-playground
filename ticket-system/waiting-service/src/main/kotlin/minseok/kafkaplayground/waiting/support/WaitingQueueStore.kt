package minseok.kafkaplayground.waiting.support

interface WaitingQueueStore {
    fun enqueue(queueCode: String, ticketId: Long, sequence: Long)
    fun remove(queueCode: String, ticketId: Long)
    fun position(queueCode: String, ticketId: Long): Long?
}
