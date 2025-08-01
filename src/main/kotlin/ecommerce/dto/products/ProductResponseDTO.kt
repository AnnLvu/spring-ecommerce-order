package ecommerce.dto.products

import java.time.LocalDateTime

class ProductResponseDTO(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val options: List<OptionResponseDTO>,
    val createdAt: LocalDateTime,
)
