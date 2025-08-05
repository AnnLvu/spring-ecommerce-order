package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Option(
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "price", nullable = false)
    var price: Double,
    @Column(name = "quantity", nullable = false)
    var quantity: Int,
    @Column(name = "image_url", nullable = false)
    var imageUrl: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        require(quantity in MIN_QUANTITY..MAX_QUANTITY) { "quantity must be positive" }
        require(price >= MIN_PRICE) { "price must be greater than 0.01" }
        require(name.trim().isNotEmpty()) { "name must not be empty" }
        require(name.length <= NAME_MAX_LENGTH) { "name must be less than 50 characters" }
        require(name.matches(NAME_PATTERN)) { "name must match pattern" }
        require(imageUrl.matches(URL_PATTERN)) { "image URL must valid url" }
    }

    companion object {
        private const val NAME_MAX_LENGTH = 50
        private const val MIN_PRICE = 0.01
        private const val MIN_QUANTITY = 1
        private const val MAX_QUANTITY = 100_000_000
        private val NAME_PATTERN = Regex("^[a-zA-Z0-9 ()\\[\\]+\\-&/_]+$")
        private val URL_PATTERN = Regex("^https?://.*\\.(png|jpg|jpeg|gif|webp)$")
    }
}
