package ecommerce.dto

import ecommerce.dto.user.UserRequestDto
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserRequestDtoTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should pass validation for valid user`() {
        val dto =
            UserRequestDto(
                "John Doe",
                "user@example.com",
                "securePass",
            )

        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should fail validation when name is blank`(name: String) {
        val dto =
            UserRequestDto(
                name,
                "user@example.com",
                "securePass",
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Name cannot be blank")
    }

    @ParameterizedTest
    @ValueSource(strings = ["   ", "user@", "user.com", "user@.com"])
    fun `should fail validation when email is invalid`(email: String) {
        val dto =
            UserRequestDto(
                "John Doe",
                email,
                "securePass",
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message)
            .isIn("Email cannot be blank", "Should be a valid email address")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "123", "abc12"])
    fun `should fail validation when password is too short`(password: String) {
        val dto =
            UserRequestDto(
                "John Doe",
                "user@example.com",
                password,
            )

        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Password must be at least 6 characters")
    }
}
