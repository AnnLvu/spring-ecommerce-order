package ecommerce.mapper

import ecommerce.dto.products.ProductDTO
import ecommerce.entity.Product

fun Product.toDto(): ProductDTO {
    return ProductDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        imageUrl = this.imageUrl,
        quantity = this.quantity,
    )
}
