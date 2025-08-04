package ecommerce.dto.cartProduct

class CartProductRequestDto(
    val optionId: Long,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
)
