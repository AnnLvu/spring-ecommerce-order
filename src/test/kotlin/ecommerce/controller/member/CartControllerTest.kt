package ecommerce.controller.member

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.products.ProductDTO
import ecommerce.enums.UserRole
import ecommerce.exception.UserCredentialException
import ecommerce.mapper.UserRowMapper
import ecommerce.repository.ProductRepository
import ecommerce.service.MemberAuthService
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CartControllerTest {
    private lateinit var token: String

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var memberAuthService: MemberAuthService

    @Autowired
    lateinit var userRowMapper: UserRowMapper

    @Autowired
    lateinit var productRepository: ProductRepository

    @BeforeEach
    fun init() {
        val sql = "select * from users where role = '${UserRole.USER}'"
        val result = jdbcTemplate.query(sql, userRowMapper).first()
        token =
            memberAuthService.logIn(
                LoginRequest(
                    result.email, result.password,
                ),
            )
    }

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
        val productId =
            productRepository.create(
                ProductDTO(
                    name = "addProduct",
                    description = "add product",
                    price = 10.0,
                    quantity = 10,
                    imageUrl = "",
                ),
            )
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().post("/api/member/cart/$productId")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    fun removeProduct() {
        val productId =
            productRepository.create(
                ProductDTO(
                    name = "removeProduct",
                    description = "add product",
                    price = 10.0,
                    quantity = 10,
                    imageUrl = "",
                ),
            )

        RestAssured
            .given().log().all()
            .header("Authorization", token)
            .`when`().post("/api/member/cart/$productId")
            .then().log().all().extract()

        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().delete("/api/member/cart/$productId")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun `Admin tries to check cart`() {
        val sql = "select * from users where role = '${UserRole.ADMIN}'"
        val result = jdbcTemplate.query(sql, userRowMapper).first()
        assertThrows<UserCredentialException> {
            memberAuthService.logIn(
                LoginRequest(
                    result.email,
                    result.password,
                ),
            )
        }
    }
}
