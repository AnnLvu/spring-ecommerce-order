package ecommerce.controller.admin

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.enums.UserRole
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.repository.ProductRepository
import ecommerce.repository.UserRepository
import ecommerce.service.AdminAuthService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminProductControllerTest {
    private lateinit var token: String

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var adminAuthService: AdminAuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun initBefore() {
        val user =
            userRepository.save(
                User(
                    name = "testUser",
                    email = "admin@testing.com",
                    password = "testPassword",
                    role = UserRole.ADMIN,
                ),
            )
        token = adminAuthService.login(LoginRequest(user.email, user.password))
    }

    @AfterEach
    fun initAfter() {
        val user = userRepository.findByEmail("admin@testing.com").orElseThrow()
        userRepository.delete(user)
        productRepository.deleteAll()
    }

    @Test
    fun create() {
        val actual =
            ProductDTO(
                name = "test",
                price = 10.0,
                imageUrl = "http://localhost:8080/image/upload/product1.jpg",
            )
        val response =
            RestAssured
                .given().log().all()
                .body(actual)
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().post("/api/admin/products")
                .then().log().all().extract()

        val expected = productRepository.findByName("ControllerCre").orElse(null)

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    fun `throws error if validation fails create`() {
        val product =
            ProductDTO(
                name = "shouldFailTheTest",
                price = 10.0,
                imageUrl = "http://localhost:8080/image/upload/product1.jpg",
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
        val product = createProduct("Get One Product")
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().get("/api/admin/products/${product.id}")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun update() {
        val product = createProduct("Update")
        val response =
            RestAssured
                .given().log().all()
                .body(
                    ProductDTO(
                        name = "Product2",
                        price = 10.0,
                        imageUrl = "http://localhost:8080/image/upload/product1.jpg",
                    ),
                )
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .`when`().put("/api/admin/products/${product.id}")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun patch() {
        val product = createProduct("Patch")
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
                .`when`().patch("/api/admin/products/${product.id}")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun delete() {
        val product = createProduct("Delete")
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", token)
                .`when`().delete("/api/admin/products/${product.id}")
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

    private fun createProduct(name: String): Product {
        return productRepository.save(
            Product(
                name = name,
                price = 10.0,
                imageUrl = "url.com",
            ),
        )
    }
}
