package ecommerce.extensions

import ecommerce.dto.products.OptionDto
import ecommerce.dto.products.OptionResponseDto
import ecommerce.model.Option

fun OptionDto.toEntity(): Option {
    return Option(
        name,
        price,
        quantity,
        imageUrl,
    )
}

fun Option.toDto(): OptionResponseDto {
    return OptionResponseDto(
        id,
        name,
        price,
        quantity,
        imageUrl,
    )
}
