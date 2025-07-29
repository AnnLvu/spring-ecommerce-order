package ecommerce.entity

class CartProductResponse(
    val productId: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
)
