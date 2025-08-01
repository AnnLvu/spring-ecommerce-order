package ecommerce.utils.extensions

import ecommerce.dto.products.OptionDTO
import ecommerce.dto.products.OptionResponseDTO
import ecommerce.model.Option

fun OptionDTO.toEntity(): Option {
    return Option(
        name,
        price,
        quantity,
        imageUrl,
    )
}

fun Option.toDTO(): OptionResponseDTO {
    return OptionResponseDTO(
        id,
        name,
        price,
        quantity,
        imageUrl,
    )
}
