package ecommerce.dto.stripe

data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val paymentMethodId: String,
)
