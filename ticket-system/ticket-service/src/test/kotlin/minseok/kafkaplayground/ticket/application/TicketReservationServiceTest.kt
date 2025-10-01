package minseok.kafkaplayground.ticket.application

import io.mockk.Runs
import io.mockk.bdd.given
import io.mockk.bdd.then
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import minseok.kafkaplayground.common.BaseEntity
import minseok.kafkaplayground.ticket.application.command.ReserveTicketCommand
import minseok.kafkaplayground.ticket.domain.TicketReservation
import minseok.kafkaplayground.ticket.domain.TicketReservationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TicketReservationServiceTest {
    private val ticketReservationRepository = mockk<TicketReservationRepository>()
    private val ticketReservationPublisher = mockk<TicketReservationPublisher>()
    private val ticketReservationService = TicketReservationService(ticketReservationRepository, ticketReservationPublisher)

    @Test
    fun `reserve should persist reservation and publish event`() {
        val command = ReserveTicketCommand(memberId = 42L, seatNumber = "A-1")
        val persistedReservation =
            TicketReservation(
                memberId = command.memberId,
                seatNumber = command.seatNumber,
            ).withId(1001L)

        val savedSlot = slot<TicketReservation>()
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { persistedReservation }
        given { ticketReservationPublisher.publishReserved(persistedReservation) } just Runs

        val result = ticketReservationService.reserve(command)

        assertThat(result).isSameAs(persistedReservation)
        assertThat(savedSlot.captured.memberId).isEqualTo(command.memberId)
        assertThat(savedSlot.captured.seatNumber).isEqualTo(command.seatNumber)
        then(exactly = 1) { ticketReservationRepository.save(any()) }
        then(exactly = 1) { ticketReservationPublisher.publishReserved(persistedReservation) }
    }

    private fun TicketReservation.withId(id: Long): TicketReservation {
        val field = BaseEntity::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(this, id)
        return this
    }
}
