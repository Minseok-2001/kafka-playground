package minseok.kafkaplayground.ticket.adapter

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import minseok.kafkaplayground.ticket.application.ReserveTicketCommand
import minseok.kafkaplayground.ticket.application.TicketReservationService
import minseok.kafkaplayground.ticket.domain.TicketReservation
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tickets")
@Validated
class TicketReservationController(
    private val ticketReservationService: TicketReservationService,
) {
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun reserve(@Valid @RequestBody request: ReserveTicketRequest): TicketReservationResponse {
        val reservation = ticketReservationService.reserve(
            ReserveTicketCommand(
                memberId = request.memberId,
                seatNumber = request.seatNumber,
            ),
        )
        return reservation.toResponse()
    }

    @GetMapping("/reservations/{memberId}")
    fun findByMember(@PathVariable memberId: Long): List<TicketReservationResponse> {
        return ticketReservationService.findByMember(memberId).map { it.toResponse() }
    }
}

data class ReserveTicketRequest(
    @field:NotNull
    val memberId: Long,
    @field:NotBlank
    val seatNumber: String,
)

data class TicketReservationResponse(
    val id: Long,
    val memberId: Long,
    val seatNumber: String,
    val status: String,
)

private fun TicketReservation.toResponse(): TicketReservationResponse = TicketReservationResponse(
    id = id,
    memberId = memberId,
    seatNumber = seatNumber,
    status = status.name,
)
