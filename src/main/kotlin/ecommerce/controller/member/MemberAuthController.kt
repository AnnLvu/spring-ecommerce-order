package ecommerce.controller.member

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.response.TokenResponse
import ecommerce.dto.user.UserRequestDTO
import ecommerce.service.MemberAuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member/auth")
class MemberAuthController(
    val memberAuthService: MemberAuthService,
) {
    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody @Valid userDTO: UserRequestDTO,
    ): ResponseEntity<TokenResponse> {
        val userCreateResponse = memberAuthService.signUp(userDTO)
        return ResponseEntity.created(userCreateResponse.uri)
            .body(TokenResponse(userCreateResponse.token))
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid loginRequest: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val token = memberAuthService.logIn(loginRequest)
        return ResponseEntity.ok().body(TokenResponse(token))
    }
}
