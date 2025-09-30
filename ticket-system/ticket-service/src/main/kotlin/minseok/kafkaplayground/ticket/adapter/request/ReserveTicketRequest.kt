package minseok.kafkaplayground.ticket.adapter.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ReserveTicketRequest(
    @field:NotNull
    val memberId: Long,
    @field:NotBlank
    val seatNumber: String,
)
