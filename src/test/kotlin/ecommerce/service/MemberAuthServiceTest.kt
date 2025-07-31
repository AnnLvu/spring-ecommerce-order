package ecommerce.service

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.user.UserRequestDTO
import ecommerce.enums.UserRole
import ecommerce.model.User
import ecommerce.repository.UserRepository
import ecommerce.utils.exception.UserAlreadyExistsException
import ecommerce.utils.exception.UserCredentialException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MemberAuthServiceTest {
    @Autowired
    private lateinit var memberAuthService: MemberAuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `throws error if already exists signUp`() {
        val userDTO =
            UserRequestDTO(
                name = "test",
                password = "test123",
                email = "signUpError@test.com",
            )
        val member =
            User(
                email = userDTO.email,
                password = userDTO.password,
                name = userDTO.name,
                role = UserRole.USER,
            )
        userRepository.save(member)
        assertThrows<UserAlreadyExistsException> { memberAuthService.signUp(userDTO) }
    }

    @Test
    fun signUp() {
        val userDTO =
            UserRequestDTO(
                name = "test",
                password = "test123",
                email = "signUp@test.com",
            )
        val result = memberAuthService.signUp(userDTO)
        assertThat(result.token).isNotEmpty
        assertThat(result.uri).isNotNull
    }

    @Test
    fun `No user found at signIn with email`() {
        val loginRequest =
            LoginRequest(
                password = "test123",
                email = "signInError@test.com",
            )
        assertThrows<UserCredentialException> { memberAuthService.login(loginRequest) }
    }

    @Test
    fun `No user found at signIn with wrong password`() {
        val memberUser =
            User(
                name = "test",
                password = "test123",
                email = "signInErrorPassword@test.com",
                role = UserRole.USER,
            )
        userRepository.save(memberUser)
        val loginRequest =
            LoginRequest(
                email = memberUser.email,
                password = "test123456",
            )
        assertThrows<UserCredentialException> { memberAuthService.login(loginRequest) }
    }

    @Test
    fun signIn() {
        val memberUser =
            User(
                name = "test",
                password = "test123",
                email = "signInError@test.com",
                role = UserRole.USER,
            )
        userRepository.save(memberUser)
        val loginRequest =
            LoginRequest(
                memberUser.email,
                memberUser.password,
            )
        assertThat(memberAuthService.login(loginRequest)).isNotEmpty
    }
}
