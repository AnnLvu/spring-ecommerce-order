package ecommerce.utils.extensions

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.model.Product

fun Product.toProductDTO(): ProductResponseDTO {
    return ProductResponseDTO(
        this.id,
        this.name,
        this.price,
        this.imageUrl,
        this.quantity,
        this.createdAt,
    )
}
