package ecommerce.entity

class CartProduct(
    val id: Long,
    val cartId: Long,
    val productID: Long,
    val quantity: Int,
)
