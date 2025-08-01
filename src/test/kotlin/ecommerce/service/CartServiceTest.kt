package ecommerce.service

import ecommerce.dto.user.UserRequestDTO
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.repository.CartStatisticRepository
import ecommerce.repository.ProductRepository
import ecommerce.repository.UserRepository
import ecommerce.utils.exception.EntityNotFoundException
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class CartServiceTest {
    lateinit var user: User
    lateinit var product: Product

    @Autowired
    lateinit var cartService: CartService

    @Autowired
    lateinit var memberAuthService: MemberAuthService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var cartStatisticRepository: CartStatisticRepository

    @BeforeEach
    fun initBefore() {
        memberAuthService.signUp(
            UserRequestDTO(
                name = "testUser",
                email = "user@testing.com",
                password = "testPassword",
            ),
        )
        product =
            productRepository.save(
                Product(
                    name = "testProduct",
                    price = 10.0,
                    imageUrl = "testImageUrl",
                    quantity = 10,
                ),
            )
        user = userRepository.findByEmail("user@testing.com").orElse(null)
    }

    @AfterEach
    fun initAfter() {
        cartStatisticRepository.deleteAll()
        userRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun findCartProduct() {
        cartService.addProductToCart(user, product.id)

        assertThat(cartService.getCartProducts(user).products.size).isEqualTo(1)
    }

    @Test fun `throw error invalid product addProductToCart`() {
        assertThrows<EntityNotFoundException> { cartService.addProductToCart(user, -1) }
    }

    @Test fun `increase quantity for addProductToCart`() {
        cartService.addProductToCart(user, product.id)
        cartService.addProductToCart(user, product.id)

        assertThat(cartService.getCartProducts(user).products.first().quantity).isEqualTo(2)
    }

    @Test fun `throw error if no cart found`() {
        val testUser =
            userRepository.save(
                User(
                    name = "testUser",
                    email = "nocartemail@temp.com",
                    password = "testPassword",
                ),
            )

        assertThrows<EntityNotFoundException> { cartService.getCartProducts(testUser) }
    }

    @Test
    fun removeProductFromCart() {
        cartService.addProductToCart(user, product.id)
        cartService.removeProductFromCart(user, product.id)

        assertThat(cartService.getCartProducts(user).products).isEmpty()
    }

    @Test
    fun `throws error if no product there removeProductFromCart`() {
        assertThrows<EntityNotFoundException> { cartService.removeProductFromCart(user, product.id) }
    }

    @Test
    fun `quantity reduced removeProductFromCart`() {
        cartService.addProductToCart(user, product.id)
        cartService.addProductToCart(user, product.id)
        cartService.removeProductFromCart(user, product.id)

        assertThat(cartService.getCartProducts(user).products.first().quantity).isEqualTo(1)
    }
}
