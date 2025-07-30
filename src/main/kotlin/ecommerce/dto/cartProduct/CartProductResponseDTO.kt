package ecommerce.dto.cartProduct

class CartProductResponseDTO(
    val productId: Long,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
)
