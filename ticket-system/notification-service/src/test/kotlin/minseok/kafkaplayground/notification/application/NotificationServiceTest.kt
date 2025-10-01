package minseok.kafkaplayground.notification.application

import io.mockk.Called
import io.mockk.MockKMatcherScope
import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import minseok.kafkaplayground.notification.application.command.CreateNotificationCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationFailedCommand
import minseok.kafkaplayground.notification.application.command.MarkNotificationSentCommand
import minseok.kafkaplayground.notification.domain.NotificationRequest
import minseok.kafkaplayground.notification.domain.NotificationRequestRepository
import minseok.kafkaplayground.notification.domain.NotificationStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationServiceTest {
    private val notificationRequestRepository = mockk<NotificationRequestRepository>()
    private val notificationEventPublisher = mockk<NotificationEventPublisher>(relaxed = true)
    private val clock = Clock.fixed(Instant.parse("2025-10-01T00:00:00Z"), ZoneOffset.UTC)
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setUp() {
        notificationService =
            NotificationService(
                notificationRequestRepository,
                notificationEventPublisher,
                clock,
            )
    }

    @Test
    fun `should create immediate notification and dispatch`() {
        val savedNotification = slot<NotificationRequest>()
        given { notificationRequestRepository.save(capture(savedNotification)) } answers {
            val entity = savedNotification.captured
            setIdIfRequired(entity, 500)
            entity
        }

        val result =
            notificationService.create(
                CreateNotificationCommand(
                    memberId = 10,
                    channel = "EMAIL",
                    subject = "Welcome",
                    body = "Hello",
                    scheduledAt = null,
                ),
            )

        assertEquals(NotificationStatus.SENT, result.status)
        assertTrue(result.sentAt != null)
        val publishedAt = slot<Instant>()
        then { notificationEventPublisher.publish(500, 10, "EMAIL", "SENT", capture(publishedAt)) }
    }

    @Test
    fun `should queue future notification`() {
        val savedNotification = slot<NotificationRequest>()
        given { notificationRequestRepository.save(capture(savedNotification)) } answers {
            val entity = savedNotification.captured
            setIdIfRequired(entity, 501)
            entity
        }

        val futureTime = Instant.parse("2025-10-02T00:00:00Z")
        val result =
            notificationService.create(
                CreateNotificationCommand(
                    memberId = 11,
                    channel = "PUSH",
                    subject = "Reminder",
                    body = "Wait",
                    scheduledAt = futureTime,
                ),
            )

        assertEquals(NotificationStatus.PENDING, result.status)
        verify { notificationEventPublisher wasNot Called }
    }

    @Test
    fun `should mark notification as sent`() {
        val notification =
            NotificationRequest(
                memberId = 12,
                channel = "SMS",
                subject = "Code",
                body = "1234",
                scheduledAt = null,
            )
        setId(notification, 700)

        given { notificationRequestRepository.findById(notification.id) } returns Optional.of(notification)
        given { notificationRequestRepository.save(notification) } returns notification

        val result = notificationService.markSent(MarkNotificationSentCommand(notificationId = 700))

        assertEquals(NotificationStatus.SENT, result.status)
        val publishedAt = slot<Instant>()
        then { notificationEventPublisher.publish(700, 12, "SMS", "SENT", capture(publishedAt)) }
    }

    @Test
    fun `should mark notification as failed`() {
        val notification =
            NotificationRequest(
                memberId = 13,
                channel = "EMAIL",
                subject = "Reset",
                body = "Reset link",
                scheduledAt = null,
            )
        setId(notification, 900)

        given { notificationRequestRepository.findById(notification.id) } returns Optional.of(notification)
        given { notificationRequestRepository.save(notification) } returns notification

        val result =
            notificationService.markFailed(
                MarkNotificationFailedCommand(
                    notificationId = 900,
                    reason = "smtp down",
                ),
            )

        assertEquals(NotificationStatus.FAILED, result.status)
        assertEquals("smtp down", result.failureReason)
        val publishedAt = slot<Instant>()
        then { notificationEventPublisher.publish(900, 13, "EMAIL", "FAILED", capture(publishedAt)) }
    }

    private fun setId(
        entity: NotificationRequest,
        value: Long,
    ) {
        val field = entity.javaClass.superclass!!.getDeclaredField("id")
        field.isAccessible = true
        field.setLong(entity, value)
    }

    private fun setIdIfRequired(
        entity: NotificationRequest,
        value: Long,
    ) {
        if (entity.id == 0L) {
            setId(entity, value)
        }
    }
}

private fun <T> given(block: MockKMatcherScope.() -> T) = every(block)

private fun then(block: MockKVerificationScope.() -> Unit) = verify(verifyBlock = block)
