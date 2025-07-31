package ecommerce.model

import ecommerce.utils.exception.EntityNotFoundException
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "carts")
class Cart(
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User,
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartProduct> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun addProduct(
        product: Product,
        quantity: Int = 1,
    ): CartProduct {
        val existing = findProduct(product)
        return if (existing != null) {
            existing.quantity += quantity
            existing
        } else {
            val newItem = CartProduct(cart = this, product = product, quantity = quantity)
            items.add(newItem)
            newItem
        }
    }

    fun decrementProduct(
        product: Product,
        decrement: Int = 1,
    ) {
        require(decrement > 0) { "Quantity to decrement must be greater than 0" }

        val existing =
            findProduct(product)
                ?: throw EntityNotFoundException("Product with id ${product.id} not found")
        if (existing.quantity > decrement) {
            existing.quantity -= decrement
        } else {
            items.remove(existing)
        }
    }

    fun clear() {
        items.clear()
    }

    private fun findProduct(product: Product): CartProduct? {
        return items.find { it.product == product }
    }
}
