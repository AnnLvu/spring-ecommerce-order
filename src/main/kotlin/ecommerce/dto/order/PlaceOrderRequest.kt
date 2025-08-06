package ecommerce.dto.order

import ecommerce.dto.stripe.PaymentResponse

data class PlaceOrderRequest(
    val productOptionId: Long,
    val quantity: Int,
    val currency: String,
    val paymentMethod: PaymentResponse
)
