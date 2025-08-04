package ecommerce.model

import ecommerce.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CartTest {

    private fun createOption(
        name: String = "Test Option",
        price: Double = 10.0,
        quantity: Int = 5,
        imageUrl: String = "http://localhost/image.png"
    ) = Option(name, price, quantity, imageUrl)


    @Test
    fun `addProduct should add a new product when cart is empty`() {
        val cart = Cart()
        val option = createOption()

        val added = cart.addProduct(option)

        assertThat(cart.items).hasSize(1)
        assertThat(added.option).isEqualTo(option)
        assertThat(added.quantity).isEqualTo(1)
    }

    @Test
    fun `addProduct should increase quantity if product already exists`() {
        val cart = Cart()
        val option = createOption()
        cart.addProduct(option, quantity = 1)

        val updated = cart.addProduct(option, quantity = 2)

        assertThat(cart.items).hasSize(1)
        assertThat(updated.quantity).isEqualTo(3)
    }

    @Test
    fun `decrementProduct should reduce quantity if greater than decrement`() {
        val cart = Cart()
        val option = createOption()
        cart.addProduct(option, quantity = 5)

        cart.decrementProduct(option, decrement = 2)

        val item = cart.items.first()
        assertThat(item.quantity).isEqualTo(3)
    }

    @Test
    fun `decrementProduct should remove item if quantity less or equal to decrement`() {
        val cart = Cart()
        val option = createOption()
        cart.addProduct(option, quantity = 2)

        cart.decrementProduct(option, decrement = 2)

        assertThat(cart.items).isEmpty()
    }

    @Test
    fun `decrementProduct should throw if product does not exist`() {
        val cart = Cart()
        val option = createOption()

        assertThatThrownBy { cart.decrementProduct(option) }
            .isInstanceOf(EntityNotFoundException::class.java)
            .hasMessageContaining("${option.id}")
    }

    @Test
    fun `decrementProduct should throw if decrement is zero or negative`() {
        val cart = Cart()
        val option = createOption()
        cart.addProduct(option)

        assertThatThrownBy { cart.decrementProduct(option, decrement = 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("must be greater than 0")

        assertThatThrownBy { cart.decrementProduct(option, decrement = -1) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `clear should remove all items from the cart`() {
        val cart = Cart()
        val option1 = createOption("Option1")
        val option2 = createOption("Option2")
        cart.addProduct(option1)
        cart.addProduct(option2)

        cart.clear()

        assertThat(cart.items).isEmpty()
    }
}
