package ecommerce.controller.guest

import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class GuestProductControllerTest {
    @Test
    fun listProducts() {
        val response =
            RestAssured
                .given().log().all()
                .`when`().get("api/products")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }
}
