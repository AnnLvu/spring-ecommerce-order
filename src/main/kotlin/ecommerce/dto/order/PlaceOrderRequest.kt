package ecommerce.dto.order

data class PlaceOrderRequest(
    val currency: String,
    val paymentMethodId: String,
)
