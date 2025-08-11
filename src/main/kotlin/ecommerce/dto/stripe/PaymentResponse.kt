package ecommerce.dto.stripe

data class PaymentResponse(
    val id: String,
    val amount: Long,
    val currency: String,
    val status: String,
    val declineCode: String? = null,
)
