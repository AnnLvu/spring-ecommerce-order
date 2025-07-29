package ecommerce.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class UserRequestDTO(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Should be a valid email address")
    val email: String,
    @field:Length(min = 6, message = "Password must be at least 6 characters")
    val password: String,
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,
)
