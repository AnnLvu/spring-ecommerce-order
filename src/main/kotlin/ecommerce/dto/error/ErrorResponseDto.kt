package ecommerce.dto.error

import java.time.Instant

class ErrorResponseDto(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: Any,
    val path: String? = null,
)
