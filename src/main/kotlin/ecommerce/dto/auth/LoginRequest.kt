package ecommerce.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class LoginRequest(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Please enter a valid email address")
    val email: String,
    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 6, message = "Password must have at least 6 characters")
    val password: String,
)
