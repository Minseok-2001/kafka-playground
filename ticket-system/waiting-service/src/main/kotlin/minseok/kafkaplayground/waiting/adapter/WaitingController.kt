package minseok.kafkaplayground.waiting.adapter

import jakarta.validation.Valid
import minseok.kafkaplayground.waiting.adapter.request.AdmitWaitingTicketRequest
import minseok.kafkaplayground.waiting.adapter.request.IssueWaitingTicketRequest
import minseok.kafkaplayground.waiting.adapter.response.WaitingStatusResponse
import minseok.kafkaplayground.waiting.adapter.response.WaitingTicketResponse
import minseok.kafkaplayground.waiting.adapter.response.toResponse
import minseok.kafkaplayground.waiting.application.WaitingService
import minseok.kafkaplayground.waiting.application.command.AdmitWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.IssueWaitingTicketCommand
import minseok.kafkaplayground.waiting.application.command.WaitingStatusQuery
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
@RequestMapping("/api/waiting")
@Validated
class WaitingController(
    private val waitingService: WaitingService,
) {
    @PostMapping("/{queueCode}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun issueTicket(
        @PathVariable queueCode: String,
        @Valid @RequestBody request: IssueWaitingTicketRequest,
    ): WaitingTicketResponse {
        val command = IssueWaitingTicketCommand(
            queueCode = queueCode,
            memberId = request.memberId,
        )
        return waitingService.issueTicket(command).toResponse()
    }

    @PostMapping("/tickets/{ticketId}/admit")
    fun admitTicket(
        @PathVariable ticketId: Long,
        @Valid @RequestBody request: AdmitWaitingTicketRequest,
    ): WaitingTicketResponse {
        val command = AdmitWaitingTicketCommand(
            ticketId = ticketId,
            estimatedSecondsUntilEntry = request.estimatedSecondsUntilEntry,
        )
        return waitingService.admitTicket(command).toResponse()
    }

    @GetMapping("/{queueCode}/tickets/{memberId}")
    fun status(
        @PathVariable queueCode: String,
        @PathVariable memberId: Long,
    ): WaitingStatusResponse {
        val query = WaitingStatusQuery(queueCode = queueCode, memberId = memberId)
        return waitingService.getStatus(query).toResponse()
    }
}
