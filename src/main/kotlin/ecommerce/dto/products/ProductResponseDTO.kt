package ecommerce.dto.products

import java.time.LocalDateTime

class ProductResponseDTO(
    val id: Long,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1,
    val createdAt: LocalDateTime,
)
