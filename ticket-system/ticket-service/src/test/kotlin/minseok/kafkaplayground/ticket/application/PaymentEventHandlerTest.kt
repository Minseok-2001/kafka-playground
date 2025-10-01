package minseok.kafkaplayground.ticket.application

import io.mockk.bdd.given
import io.mockk.mockk
import io.mockk.slot
import minseok.kafkaplayground.common.BaseEntity
import minseok.kafkaplayground.common.event.PaymentTransactionEvent
import minseok.kafkaplayground.ticket.domain.ReservationStatus
import minseok.kafkaplayground.ticket.domain.TicketReservation
import minseok.kafkaplayground.ticket.domain.TicketReservationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

class PaymentEventHandlerTest {
    private val ticketReservationRepository = mockk<TicketReservationRepository>()
    private val handler = PaymentEventHandler(ticketReservationRepository)

    @Test
    fun `handle should confirm reservation when payment approved`() {
        val reservation =
            TicketReservation(
                memberId = 77L,
                seatNumber = "B-12",
            ).withId(501L)
        val savedSlot = slot<TicketReservation>()

        given { ticketReservationRepository.findById(reservation.id) } answers { Optional.of(reservation) }
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        handler.handle(
            PaymentTransactionEvent(
                transactionId = 9001L,
                reservationId = reservation.id,
                status = "APPROVED",
            ),
        )

        assertThat(savedSlot.isCaptured).isTrue()
        assertThat(savedSlot.captured.status).isEqualTo(ReservationStatus.CONFIRMED)
    }

    @Test
    fun `handle should compensate reservation when payment compensated`() {
        val reservation =
            TicketReservation(
                memberId = 88L,
                seatNumber = "C-3",
            ).withId(777L)
        val savedSlot = slot<TicketReservation>()

        given { ticketReservationRepository.findById(reservation.id) } answers { Optional.of(reservation) }
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        handler.handle(
            PaymentTransactionEvent(
                transactionId = 9002L,
                reservationId = reservation.id,
                status = "COMPENSATED",
            ),
        )

        assertThat(savedSlot.isCaptured).isTrue()
        assertThat(savedSlot.captured.status).isEqualTo(ReservationStatus.COMPENSATED)
    }

    @Test
    fun `handle should cancel reservation when payment rejected`() {
        val reservation =
            TicketReservation(
                memberId = 99L,
                seatNumber = "D-4",
            ).withId(888L)
        val savedSlot = slot<TicketReservation>()

        given { ticketReservationRepository.findById(reservation.id) } answers { Optional.of(reservation) }
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        handler.handle(
            PaymentTransactionEvent(
                transactionId = 9003L,
                reservationId = reservation.id,
                status = "REJECTED",
            ),
        )

        assertThat(savedSlot.isCaptured).isTrue()
        assertThat(savedSlot.captured.status).isEqualTo(ReservationStatus.CANCELLED)
    }

    @Test
    fun `handle should skip when reservation does not exist`() {
        val savedSlot = slot<TicketReservation>()
        given { ticketReservationRepository.findById(1234L) } answers { Optional.empty<TicketReservation>() }
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        handler.handle(
            PaymentTransactionEvent(
                transactionId = 9004L,
                reservationId = 1234L,
                status = "APPROVED",
            ),
        )

        assertThat(savedSlot.isCaptured).isFalse()
    }

    @Test
    fun `handle should not persist when status unsupported`() {
        val reservation =
            TicketReservation(
                memberId = 55L,
                seatNumber = "E-5",
            ).withId(999L)
        val savedSlot = slot<TicketReservation>()

        given { ticketReservationRepository.findById(reservation.id) } answers { Optional.of(reservation) }
        given { ticketReservationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        handler.handle(
            PaymentTransactionEvent(
                transactionId = 9005L,
                reservationId = reservation.id,
                status = "UNKNOWN",
            ),
        )

        assertThat(savedSlot.isCaptured).isFalse()
        assertThat(reservation.status).isEqualTo(ReservationStatus.PENDING)
    }

    private fun TicketReservation.withId(id: Long): TicketReservation {
        val field = BaseEntity::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(this, id)
        return this
    }
}
