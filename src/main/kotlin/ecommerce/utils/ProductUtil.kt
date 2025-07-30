package ecommerce.utils

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.entity.Product

fun Product.toProductDTO(): ProductResponseDTO {
    return ProductResponseDTO(
        id = this.id,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
    )
}
