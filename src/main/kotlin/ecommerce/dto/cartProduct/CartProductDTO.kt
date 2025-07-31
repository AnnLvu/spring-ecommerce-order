package ecommerce.dto.cartProduct

class CartProductDTO(
    val productId: Long,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
)
