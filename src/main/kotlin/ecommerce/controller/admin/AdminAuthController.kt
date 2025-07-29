package ecommerce.controller.admin

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.response.TokenResponse
import ecommerce.service.AdminAuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/auth")
class AdminAuthController(
    private val adminAuthService: AdminAuthService,
) {
    @PostMapping("/login")
    fun signIn(
        @RequestBody @Valid loginRequest: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        val token = adminAuthService.login(loginRequest)
        return ResponseEntity.ok().body(TokenResponse(token))
    }
}
