package minseok.kafkaplayground.member.adapter

import jakarta.validation.Valid
import minseok.kafkaplayground.member.adapter.request.ChangeMemberStatusRequest
import minseok.kafkaplayground.member.adapter.request.RegisterMemberRequest
import minseok.kafkaplayground.member.adapter.request.UpdateMemberRequest
import minseok.kafkaplayground.member.adapter.response.MemberResponse
import minseok.kafkaplayground.member.adapter.response.toResponse
import minseok.kafkaplayground.member.application.MemberService
import minseok.kafkaplayground.member.application.command.ChangeMemberStatusCommand
import minseok.kafkaplayground.member.application.command.RegisterMemberCommand
import minseok.kafkaplayground.member.application.command.UpdateMemberProfileCommand
import minseok.kafkaplayground.member.domain.MemberStatus
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
@Validated
class MemberController(
    private val memberService: MemberService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterMemberRequest): MemberResponse {
        val member = memberService.register(
            RegisterMemberCommand(
                email = request.email,
                nickname = request.nickname,
                notificationChannel = request.notificationChannel,
            ),
        )
        return member.toResponse()
    }

    @PutMapping("/{memberId}")
    fun update(
        @PathVariable memberId: Long,
        @Valid @RequestBody request: UpdateMemberRequest,
    ): MemberResponse {
        val member = memberService.updateProfile(
            UpdateMemberProfileCommand(
                memberId = memberId,
                nickname = request.nickname,
                notificationChannel = request.notificationChannel,
            ),
        )
        return member.toResponse()
    }

    @PostMapping("/{memberId}/status")
    fun changeStatus(
        @PathVariable memberId: Long,
        @Valid @RequestBody request: ChangeMemberStatusRequest,
    ): MemberResponse {
        val targetStatus = MemberStatus.valueOf(request.status.uppercase())
        val member = memberService.changeStatus(
            ChangeMemberStatusCommand(
                memberId = memberId,
                targetStatus = targetStatus,
            ),
        )
        return member.toResponse()
    }

    @GetMapping("/{memberId}")
    fun find(@PathVariable memberId: Long): MemberResponse {
        return memberService.find(memberId).toResponse()
    }
}
