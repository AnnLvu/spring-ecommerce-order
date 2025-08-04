package ecommerce.dto.cartStatistics

import java.time.LocalDateTime

interface TopAddedProductView {
    val productName: String
    val count: Long
    val createdAt: LocalDateTime
}
