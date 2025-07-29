package ecommerce.controller.admin

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.mapper.UserRowMapper
import ecommerce.repository.ProductRepository
import ecommerce.service.AdminAuthService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminProductControllerTest {
    private lateinit var token: String

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var userRowMapper: UserRowMapper

    @Autowired
    private lateinit var adminAuthService: AdminAuthService

    @BeforeEach
    fun init() {
        val sql = "select * from users where role = 'ADMIN'"
        val result = jdbcTemplate.query(sql, userRowMapper).first()
        token =
            adminAuthService.login(
                LoginRequest(
                    result.email, result.password,
                ),
            )
    }

    @Test
    fun create() {
        val product =
            ProductDTO(
                name = "ControllerCre",
                price = 10.0,
                imageUrl = "http://localhost:8080/image/upload/product1.jpg",
                description = "Product 1",
            )
        val response =
            RestAssured
                .given().log().all()
                .body(product)
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().post("/api/admin/products")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    fun `throws error if validation fails create`() {
        val product =
            ProductDTO(
                name = "ControllerCreControllerCreate",
                price = 10.0,
                imageUrl = "http://localhost:8080/image/upload/product1.jpg",
                description = "Product 1",
            )
        val response =
            RestAssured
                .given().log().all()
                .body(product)
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().post("/api/admin/products")
                .then().log().all().extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `Returns Products`() {
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().get("api/admin/products")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun `Returns Product`() {
        val id = createProduct("Get One Product")
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().get("/api/admin/products/$id")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun update() {
        val key = createProduct("Update")
        val response =
            RestAssured
                .given().log().all()
                .body(
                    ProductDTO(
                        name = "Product2",
                        price = 10.0,
                        imageUrl = "http://localhost:8080/image/upload/product1.jpg",
                        description = "Product 1",
                    ),
                )
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().put("/api/admin/products/$key")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun patch() {
        val key = createProduct("Patch")
        val response =
            RestAssured
                .given().log().all()
                .body(
                    ProductPatchDTO(
                        price = 19.0,
                    ),
                )
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().patch("/api/admin/products/$key")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun delete() {
        val id = createProduct("Delete")
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().delete("/api/admin/products/$id")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun `Throws NotFoundException if No id provided`() {
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().get("/api/admin/products/")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    private fun createProduct(name: String): Long {
        return productRepository.create(
            ProductDTO(
                name = name,
                price = 10.0,
                imageUrl = "url.com",
                description = "description",
            ),
        )
    }
}
