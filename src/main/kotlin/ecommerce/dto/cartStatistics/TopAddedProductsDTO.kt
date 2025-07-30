package ecommerce.dto.cartStatistics

import java.time.LocalDateTime

class TopAddedProductsDTO(
    val productName: String,
    val count: Long,
    val createdAt: LocalDateTime,
)
