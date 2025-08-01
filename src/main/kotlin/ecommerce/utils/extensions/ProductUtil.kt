package ecommerce.utils.extensions

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.model.Product

fun Product.toProductDTO(): ProductResponseDTO {
    return ProductResponseDTO(
        id,
        name,
        imageUrl,
        options.map { it.toDTO() },
        createdAt,
    )
}
