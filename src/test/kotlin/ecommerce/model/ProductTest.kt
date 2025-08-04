package ecommerce.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProductTest {

    private fun createOption(name: String = "Option1") =
        Option(name, 10.0, 5, "http://localhost/product.png")

    @Test
    fun `should create product with valid options`() {
        val option1 = createOption("Option1")
        val option2 = createOption("Option2")

        val product = Product(
            "Product Name",
            mutableListOf(option1, option2)
        )

        assertThat(product.name).isEqualTo("Product Name")
        assertThat(product.options).containsExactly(option1, option2)
        assertThat(product.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    fun `should throw if options list is empty`() {
        assertThatThrownBy {
            Product(
                "Product Name",
                mutableListOf()
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("at least one option must be specified")
    }

    @Test
    fun `should throw if options list has duplicates`() {
        val option = createOption("Option1")

        assertThatThrownBy {
            Product(
                "Product Name",
                mutableListOf(option, option)
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Options must be distinct")
    }
}
