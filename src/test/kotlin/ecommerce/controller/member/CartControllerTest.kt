package ecommerce.controller.member

import ecommerce.dto.cartProduct.CartProductDTO
import ecommerce.dto.user.UserRequestDTO
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.repository.CartStatisticsRepository
import ecommerce.repository.ProductRepository
import ecommerce.repository.UserRepository
import ecommerce.service.MemberAuthService
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CartControllerTest {
    private lateinit var token: String
    private lateinit var user: User

    @Autowired
    private lateinit var cartStatisticsRepository: CartStatisticsRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var memberAuthService: MemberAuthService

    @BeforeEach
    fun initBefore() {
        val userRequestDTO =
            UserRequestDTO(
                "testUser",
                "user@testing.com",
                "testPassword",
            )
        token = memberAuthService.signUp(userRequestDTO).token
        user = userRepository.findByEmail(userRequestDTO.email).orElse(null)
    }

    @AfterEach
    fun initAfter() {
        cartStatisticsRepository.deleteAll()
        userRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    fun getCartItems() {
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().get("/api/member/cart")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun addProduct() {
        val product =
            productRepository.save(
                Product(
                    "addProduct",
                    10.0,
                    "",
                    10,
                ),
            )
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().post("/api/member/cart/${product.id}")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    fun removeProduct() {
        val product =
            productRepository.save(
                Product(
                    "addProduct",
                    10.0,
                    "",
                    10,
                ),
            )

        RestAssured
            .given().log().all()
            .header("Authorization", token)
            .`when`().post("/api/member/cart/${product.id}")
            .then().log().all().extract()

        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().delete("/api/member/cart/${product.id}")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun clearCart() {
        val product =
            productRepository.save(
                Product(
                    "addProduct",
                    10.0,
                    "",
                    10,
                ),
            )

        // Add product
        RestAssured
            .given().log().all()
            .header("Authorization", token)
            .`when`().post("/api/member/cart/${product.id}")
            .then().log().all().extract()

        // Clear Cart
        val deleteResponse =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().delete("/api/member/cart/clear")
                .then().log().all().extract()

        // Get ALl products
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().get("/api/member/cart")
                .then().log().all().extract()

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        assertThat(response.body().jsonPath().get<List<CartProductDTO>>("products").size).isZero
    }
}
