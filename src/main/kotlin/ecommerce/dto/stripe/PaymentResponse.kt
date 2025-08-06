package ecommerce.dto.stripe

data class PaymentResponse(
    val id: Long,
    val amount: Long,
    val currency: String,
)
