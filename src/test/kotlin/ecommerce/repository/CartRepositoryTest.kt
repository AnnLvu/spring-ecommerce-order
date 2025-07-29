package ecommerce.repository

import ecommerce.entity.User
import ecommerce.enums.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CartRepositoryTest {
    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun findMembersCart() {
        val member =
            User(
                email = "findMembersCart@test.com",
                password = "test123",
                name = "test",
                role = UserRole.USER,
            )
        val userID = userRepository.create(member)
        cartRepository.createCartForUser(userID)

        val cartID = cartRepository.findMembersCart(userID)
        assertThat(cartID).isNotNull
    }

    @Test
    fun `return null for findMembersCart`() {
        val cartID = cartRepository.findMembersCart(-1)
        assertThat(cartID).isNull()
    }
}
