package ecommerce.service

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.user.UserRequestDTO
import ecommerce.entity.User
import ecommerce.enums.UserRole
import ecommerce.exception.EntityNotFoundException
import ecommerce.repository.ProductRepository
import ecommerce.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CartServiceTest {
    @Autowired
    lateinit var cartService: CartService

    @Autowired
    lateinit var memberAuthService: MemberAuthService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var productService: ProductRepository

    @Test
    fun findCartProduct() {
        val member =
            UserRequestDTO(
                email = "testfindCartProduct1@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "findCartProduct",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        cartService.addProductToCart(user?.id, productID)

        val products = cartService.getCartProducts(user?.id)
        assertThat(products.size).isEqualTo(1)
    }

    @Test fun addProductToCart() {
        val member =
            UserRequestDTO(
                email = "addProductToCarttest@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "12addProduct",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        cartService.addProductToCart(user?.id, productID)

        val products = cartService.getCartProducts(user?.id)
        assertThat(products.first().quantity).isEqualTo(1)
    }

    @Test fun `throw error invalid product addProductToCart`() {
        val member =
            UserRequestDTO(
                email = "addProductToCartE@test.com",
                name = "test",
                password = "test123",
            )

        memberAuthService.signUp(member)
        val user =
            userRepository.findByEmail(member.email)

        assertThrows<EntityNotFoundException> { cartService.addProductToCart(user?.id, -1) }
    }

    @Test fun `increase quantity for addProductToCart`() {
        val member =
            UserRequestDTO(
                email = "eaddProductToCart@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "addProductEE",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        cartService.addProductToCart(user?.id, productID)
        cartService.addProductToCart(user?.id, productID)

        val products = cartService.getCartProducts(user?.id)
        assertThat(products.first().quantity).isEqualTo(2)
    }

    @Test fun `throw error if no cart found`() {
        val member =
            User(
                email = "addProductToCart@test.com",
                name = "test",
                password = "test123",
                role = UserRole.USER,
            )
        userRepository.create(member)

        assertThrows<EntityNotFoundException> { cartService.getCartProducts(null) }
    }

    @Test
    fun removeProductFromCart() {
        val member =
            UserRequestDTO(
                email = "findCartProducttest54@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "someProduct",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        cartService.addProductToCart(user?.id, productID)
        cartService.removeProductFromCart(user?.id, productID)

        val products = cartService.getCartProducts(user?.id)
        assertThat(products).isEmpty()
    }

    @Test
    fun `throws error if no product there removeProductFromCart`() {
        val member =
            UserRequestDTO(
                email = "findCartProduct432@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "findCartProductT33",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        assertThrows<EntityNotFoundException> { cartService.removeProductFromCart(user?.id, productID) }
    }

    @Test
    fun `quantity reduced removeProductFromCart`() {
        val member =
            UserRequestDTO(
                email = "findCartProduct@test.com",
                name = "test",
                password = "test123",
            )
        val product =
            ProductDTO(
                name = "11removeProduct",
                price = 100.0,
                quantity = 10,
                description = "test",
                imageUrl = "",
            )

        memberAuthService.signUp(member)
        val productID = productService.create(product)
        val user =
            userRepository.findByEmail(member.email)

        cartService.addProductToCart(user?.id, productID)
        cartService.addProductToCart(user?.id, productID)

        cartService.removeProductFromCart(user?.id, productID)

        val products = cartService.getCartProducts(user?.id)
        assertThat(products.first().quantity).isEqualTo(1)
    }
}
