package ecommerce.dto

import ecommerce.dto.products.OptionDto
import ecommerce.dto.products.ProductDto
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProductDtoTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should pass validation for valid product`() {
        val dto =
            ProductDto(
                "Product-1",
                mutableListOf(
                    OptionDto(
                        "name",
                        10.2,
                        50,
                        "https://example.com/images/usb_c_hub.jpg",
                    ),
                ),
            )

        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["12", "HelloWorldHelloWorld"])
    fun `should fail validation for invalid name - size`(name: String) {
        val dto =
            ProductDto(
                name,
                mutableListOf(
                    OptionDto(
                        "name",
                        10.2,
                        50,
                        "https://example.com/images/usb_c_hub.jpg",
                    ),
                ),
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("name should be between 3 and 15")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "valid-but0",
            "undersc@re_",
            "tab\tchar",
            "newline\n",
            "ALL_OKAY🙂",
            "           ",
        ],
    )
    fun `should fail validation for invalid name - characters`(name: String) {
        val dto =
            ProductDto(
                name,
                mutableListOf(
                    OptionDto(
                        "name",
                        10.2,
                        50,
                        "https://example.com/images/usb_c_hub.jpg",
                    ),
                ),
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Product name contains invalid characters")
    }
}
