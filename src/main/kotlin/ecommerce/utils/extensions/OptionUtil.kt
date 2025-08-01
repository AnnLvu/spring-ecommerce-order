package ecommerce.utils.extensions

import ecommerce.dto.products.OptionDTO
import ecommerce.dto.products.OptionResponseDTO
import ecommerce.model.Option
import ecommerce.model.Product

fun OptionDTO.toEntity(product: Product): Option {
    return Option(
        name,
        price,
        quantity,
        product,
    )
}

fun Option.toDTO(): OptionResponseDTO {
    return OptionResponseDTO(
        this.id,
        this.name,
        this.price,
        this.quantity,
    )
}
