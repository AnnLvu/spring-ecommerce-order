package ecommerce.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OptionTest {
    @Test
    fun `should create option with valid data`() {
        val option =
            Option(
                "Valid Product Name",
                10.0,
                5,
                "http://localhost/product1.png",
            )

        assertThat(option.name).isEqualTo("Valid Product Name")
        assertThat(option.price).isEqualTo(10.0)
        assertThat(option.quantity).isEqualTo(5)
        assertThat(option.imageUrl).endsWith(".png")
    }

    @Test
    fun `should throw if quantity is zero or negative`() {
        assertThatThrownBy {
            Option("Test", 10.0, 0, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("quantity must be positive")

        assertThatThrownBy {
            Option("Test", 10.0, -1, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw if quantity exceeds max`() {
        assertThatThrownBy {
            Option("Test", 10.0, 100_000_001, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("quantity must be positive")
    }

    @Test
    fun `should throw if price is below minimum`() {
        assertThatThrownBy {
            Option("Test", 0.0, 10, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("price must be greater than 0.01")
    }

    @Test
    fun `should throw if name is empty or too long`() {
        assertThatThrownBy {
            Option("", 10.0, 10, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("name must not be empty")

        assertThatThrownBy {
            Option("A".repeat(51), 10.0, 10, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("name must be less than 50 characters")
    }

    @Test
    fun `should throw if name does not match pattern`() {
        assertThatThrownBy {
            Option("Invalid@Name!", 10.0, 10, "http://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("name must match pattern")
    }

    @Test
    fun `should throw if imageUrl does not match pattern`() {
        assertThatThrownBy {
            Option("Valid Name", 10.0, 10, "ftp://localhost/product1.png")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("image URL must valid url")

        assertThatThrownBy {
            Option("Valid Name", 10.0, 10, "http://localhost/product1.txt")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("image URL must valid url")
    }
}
