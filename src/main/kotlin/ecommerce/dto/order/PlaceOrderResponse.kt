package ecommerce.dto.order

data class PlaceOrderResponse(
    val orderId: Long,
    val checkoutSession: String
)
