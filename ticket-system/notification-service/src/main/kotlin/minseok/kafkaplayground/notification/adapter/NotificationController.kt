package minseok.kafkaplayground.notification.adapter

import jakarta.validation.Valid
import minseok.kafkaplayground.notification.adapter.request.CreateNotificationRequest
import minseok.kafkaplayground.notification.adapter.request.MarkNotificationFailedRequest
import minseok.kafkaplayground.notification.adapter.response.NotificationResponse
import minseok.kafkaplayground.notification.adapter.response.toResponse
import minseok.kafkaplayground.notification.application.NotificationService
import minseok.kafkaplayground.notification.application.command.CreateNotificationCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationFailedCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationSentCommand
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
@RequestMapping("/api/notifications")
@Validated
class NotificationController(
    private val notificationService: NotificationService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateNotificationRequest): NotificationResponse {
        val command = CreateNotificationCommand(
            memberId = request.memberId,
            channel = request.channel,
            subject = request.subject,
            body = request.body,
            scheduledAt = request.scheduledAt,
        )
        return notificationService.create(command).toResponse()
    }

    @PostMapping("/{notificationId}/send")
    fun send(@PathVariable notificationId: Long): NotificationResponse {
        val command = MarkNotificationSentCommand(notificationId = notificationId)
        return notificationService.markSent(command).toResponse()
    }

    @PostMapping("/{notificationId}/fail")
    fun fail(
        @PathVariable notificationId: Long,
        @Valid @RequestBody request: MarkNotificationFailedRequest,
    ): NotificationResponse {
        val command = MarkNotificationFailedCommand(
            notificationId = notificationId,
            reason = request.reason,
        )
        return notificationService.markFailed(command).toResponse()
    }

    @GetMapping("/{notificationId}")
    fun get(@PathVariable notificationId: Long): NotificationResponse {
        return notificationService.get(notificationId).toResponse()
    }
}
