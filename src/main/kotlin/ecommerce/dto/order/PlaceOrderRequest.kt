package ecommerce.dto.order

import ecommerce.dto.stripe.PaymentResponse

data class PlaceOrderRequest(
    val currency: String,
    val paymentMethod: PaymentResponse
)
