package ecommerce.dto.cartStatistics

import java.time.LocalDateTime

class TopAddedProductsDto(
    val productName: String,
    val count: Long,
    val createdAt: LocalDateTime,
)
