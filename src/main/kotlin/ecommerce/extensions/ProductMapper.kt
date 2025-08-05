package ecommerce.extensions

import ecommerce.dto.products.ProductResponseDto
import ecommerce.model.Product

fun Product.toProductDto(): ProductResponseDto {
    return ProductResponseDto(
        id,
        name,
        options.map { it.toDto() },
        createdAt,
    )
}
