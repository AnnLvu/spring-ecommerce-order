package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Option(
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "price", nullable = false)
    var price: Double,
    @Column(name = "quantity", nullable = false)
    var quantity: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(quantity in MIN_QUANTITY..MAX_QUANTITY) { "quantity must be positive" }
        require(price >= MIN_PRICE) { "price must be greater than 0.01" }
        require(name.trim().isNotEmpty()) { "name must not be empty" }
        require(name.length <= NAME_MAX_LENGTH) { "name must be less than 50 characters" }
        require(name.matches(PATTERN)) { "name must match pattern" }
    }

    companion object {
        private const val NAME_MAX_LENGTH = 50
        private const val MIN_PRICE = 0.01
        private const val MIN_QUANTITY = 1
        private const val MAX_QUANTITY = 100_000_000
        private val PATTERN = Regex("^[a-zA-Z0-9 ()\\[\\]+\\-&/_]+$")
    }
}
