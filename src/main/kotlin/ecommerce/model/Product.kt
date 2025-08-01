package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Product(
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    @Column(name = "image_url", nullable = false)
    var imageUrl: String,
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var options: MutableList<Option> = mutableListOf(),
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    init {
        val temp = options.map { it.name }.distinct()
        require(options.distinct().size == options.size) { "Options must be distinct" }
    }
}
