package ecommerce.dto

import ecommerce.dto.products.ProductDTO
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProductDTOTest {
    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should pass validation for valid product`() {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = "Product description",
                price = 10.2,
                imageUrl = "https://example.com/images/usb_c_hub.jpg",
                quantity = 20,
            )

        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["12", "HelloWorldHelloWorld"])
    fun `should fail validation for invalid name - size`(name: String) {
        val dto =
            ProductDTO(
                name = name,
                description = "Product description",
                price = 10.2,
                imageUrl = "https://example.com/images/usb_c_hub.jpg",
                quantity = 20,
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
            ProductDTO(
                name = name,
                description = "Product description",
                price = 10.2,
                imageUrl = "https://example.com/images/usb_c_hub.jpg",
                quantity = 20,
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Product name contains invalid characters")
    }

    @ParameterizedTest
    @ValueSource(strings = [" ", "12"])
    fun `should fail validation for invalid description - size`(description: String) {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = description,
                price = 10.2,
                imageUrl = "https://example.com/images/usb_c_hub.jpg",
                quantity = 20,
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Description must be greater than 3 characters")
    }

    @ParameterizedTest
    @ValueSource(doubles = [-1.3, 0.0])
    fun `should fail validation for invalid price`(price: Double) {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = "description",
                price = price,
                imageUrl = "https://example.com/images/usb_c_hub.jpg",
                quantity = 20,
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Product price must be greater than 0")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "not-a-url", "ftp://example.com/image.jpg", "http://example.com/image.txt"])
    fun `should fail validation for invalid imageUrl`(imageUrl: String) {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = "description",
                price = 10.2,
                imageUrl = imageUrl,
                quantity = 10,
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message)
            .isIn("Image URL cannot be blank", "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp")
    }

    @ParameterizedTest
    @ValueSource(ints = [-10, -1])
    fun `should fail validation for invalid quantity`(quantity: Int) {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = "description",
                price = 10.2,
                imageUrl = "https://example.com/image.png",
                quantity = quantity,
            )
        val violations = validator.validate(dto)
        assertThat(violations.firstOrNull()?.message).isEqualTo("Quantity cannot be negative")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://example.com/image.png",
            "http://example.com/photo.jpg",
            "https://cdn.site.com/pic.webp",
        ],
    )
    fun `should pass validation for valid imageUrl`(imageUrl: String) {
        val dto =
            ProductDTO(
                name = "Product-1",
                description = "description",
                price = 10.2,
                imageUrl = imageUrl,
                quantity = 10,
            )
        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }
}
