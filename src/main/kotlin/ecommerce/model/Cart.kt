package ecommerce.model

import ecommerce.utils.exception.EntityNotFoundException
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Cart(
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartProduct> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun addProduct(
        option: Option,
        quantity: Int = 1,
    ): CartProduct {
        val existing = findProduct(option)
        return if (existing != null) {
            existing.quantity += quantity
            existing
        } else {
            val newItem = CartProduct(this, option, quantity)
            items.add(newItem)
            newItem
        }
    }

    fun decrementProduct(
        option: Option,
        decrement: Int = 1,
    ) {
        require(decrement > 0) { "Quantity to decrement must be greater than 0" }

        val existing =
            findProduct(option)
                ?: throw EntityNotFoundException("Product option with id ${option.id} not found")
        if (existing.quantity > decrement) {
            existing.quantity -= decrement
        } else {
            items.remove(existing)
        }
    }

    fun clear() {
        items.clear()
    }

    private fun findProduct(option: Option): CartProduct? {
        return items.find { it.option == option }
    }
}
