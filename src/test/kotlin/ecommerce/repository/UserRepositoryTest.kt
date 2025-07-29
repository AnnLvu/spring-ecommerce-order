package ecommerce.repository

import ecommerce.entity.User
import ecommerce.enums.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Test fun existsByEmail() {
        val user =
            User(
                name = "test",
                password = "test123",
                email = "existsByEmail@test.com",
                role = UserRole.USER,
            )
        userRepository.create(user)
        val existsByEmail = userRepository.existsByEmail("existsByEmail@test.com")
        assertThat(existsByEmail).isTrue
    }

    @Test fun `returns false if not existsByEmail`() {
        val existsByEmail = userRepository.existsByEmail("none@test.com")
        assertThat(existsByEmail).isFalse
    }

    @Test
    fun create() {
        val user =
            User(
                name = "test",
                password = "test123",
                email = "create@test.com",
                role = UserRole.USER,
            )
        userRepository.create(user)
        val existsByEmail = userRepository.existsByEmail("create@test.com")
        assertThat(existsByEmail).isTrue
    }

    @Test
    fun findByEmailAndPassword() {
        val memberUser =
            User(
                name = "test",
                password = "test123",
                email = "findByEmailAndPassword@test.com",
                role = UserRole.USER,
            )
        userRepository.create(memberUser)

        val user = userRepository.findByEmailAndPassword(memberUser.email, memberUser.password)
        assertThat(user).isNotNull
    }

    @Test
    fun `returns null for findByEmailAndPassword`() {
        val memberUser =
            User(
                name = "test",
                password = "test123",
                email = "findByEmailAndPasswordFalse@test.com",
                role = UserRole.USER,
            )
        userRepository.create(memberUser)

        val user = userRepository.findByEmailAndPassword(memberUser.email, "")
        assertThat(user).isNull()
    }
}
