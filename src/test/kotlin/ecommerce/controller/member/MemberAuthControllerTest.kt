package ecommerce.controller.member

import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.user.UserRequestDTO
import ecommerce.service.MemberAuthService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MemberAuthControllerTest {
    @Autowired
    private lateinit var memberAuthService: MemberAuthService

    @Test
    fun `signIn User`() {
        val user =
            UserRequestDTO(
                name = "test",
                email = "temp@temp.com",
                password = "test-456",
            )
        val response =
            RestAssured
                .given().log().all()
                .body(user)
                .contentType(ContentType.JSON)
                .`when`().post("api/member/auth/sign-up")
                .then().log().all().extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        assertThat(response.body().jsonPath().get<String>("token")).isNotEmpty
    }

    @Test
    fun signUp() {
        val user =
            UserRequestDTO(
                name = "test",
                email = "temp2@temp.com",
                password = "test-456",
            )
        memberAuthService.signUp(user)
        val loginRequest =
            LoginRequest(
                email = "temp2@temp.com",
                password = "test-456",
            )
        val response =
            RestAssured
                .given().log().all()
                .body(loginRequest)
                .contentType(ContentType.JSON)
                .`when`().post("api/member/auth/login")
                .then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        assertThat(response.body().jsonPath().get<String>("token")).isNotEmpty
    }
}
