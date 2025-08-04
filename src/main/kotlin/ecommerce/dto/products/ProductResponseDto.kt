package ecommerce.dto.products

import java.time.LocalDateTime

class ProductResponseDto(
    val id: Long,
    val name: String,
    val options: List<OptionResponseDto>,
    val createdAt: LocalDateTime,
)
