package minseok.kafkaplayground.waiting.adapter.response

import minseok.kafkaplayground.waiting.application.WaitingTicketStatus
import minseok.kafkaplayground.waiting.domain.WaitingTicket

fun WaitingTicket.toResponse(): WaitingTicketResponse =
    WaitingTicketResponse(
        id = id,
        queueCode = queueCode,
        memberId = memberId,
        status = status.name,
        issuedSequence = issuedSequence,
        expiredAt = expiredAt,
    )

fun WaitingTicketStatus.toResponse(): WaitingStatusResponse =
    WaitingStatusResponse(
        ticketId = ticketId,
        queueCode = queueCode,
        memberId = memberId,
        status = status,
        position = position,
        issuedSequence = issuedSequence,
    )
