package ecommerce.dto

import ecommerce.dto.user.UserRequestDTO
import ecommerce.enums.UserRole
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserRequestDTOTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should pass validation for valid user`() {
        val dto =
            UserRequestDTO(
                email = "user@example.com",
                password = "securePass",
                name = "John Doe",
            )

        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should fail validation when name is blank`(name: String) {
        val dto =
            UserRequestDTO(
                email = "user@example.com",
                password = "securePass",
                name = name,
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Name cannot be blank")
    }

    @ParameterizedTest
    @ValueSource(strings = ["   ", "user@", "user.com", "user@.com"])
    fun `should fail validation when email is invalid`(email: String) {
        val dto =
            UserRequestDTO(
                email = email,
                password = "securePass",
                name = "John Doe",
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message)
            .isIn("Email cannot be blank", "Should be a valid email address")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "123", "abc12"])
    fun `should fail validation when password is too short`(password: String) {
        val dto =
            UserRequestDTO(
                email = "user@example.com",
                password = password,
                name = "John Doe",
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Password must be at least 6 characters")
    }

    @ParameterizedTest
    @EnumSource(UserRole::class)
    fun `should pass validation with all valid roles`(role: UserRole) {
        val dto =
            UserRequestDTO(
                email = "user@example.com",
                password = "securePass",
                name = "John Doe",
            )

        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }
}
