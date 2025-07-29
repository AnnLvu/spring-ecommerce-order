package ecommerce.repository

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.user.UserRequestDTO
import ecommerce.enums.CartAction
import ecommerce.service.MemberAuthService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CartStatisticsRepositoryTest {
    @Autowired
    private lateinit var memberAuthService: MemberAuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var cartStatisticsRepository: CartStatisticsRepository

    @Test
    fun create() {
        val member =
            UserRequestDTO(
                email = "cartStatisticsRepository@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "cartStatistic",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productRepository.create(product)
        val user =
            userRepository.findByEmail(member.email)

        val id = cartStatisticsRepository.create(user?.id, productID, CartAction.ADD)

        assertThat(id).isNotNull
    }

    @Test
    fun getTopAddedProducts() {
        assertThat(cartStatisticsRepository.getTopAddedProducts()).isNotEmpty
    }

    @Test
    fun membersAddedProduct() {
        assertThat(cartStatisticsRepository.getMembersWhoAddedToCart()).isNotEmpty
    }
}
