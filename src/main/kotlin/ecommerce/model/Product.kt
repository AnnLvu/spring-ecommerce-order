package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    @Column(name = "price", nullable = false)
    var price: Double,
    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1,
    @Column(name = "image_url", nullable = false)
    var imageUrl: String,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
