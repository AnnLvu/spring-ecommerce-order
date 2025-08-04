package ecommerce.extensions

import ecommerce.dto.options.OptionRequestDto
import ecommerce.dto.options.OptionResponseDto
import ecommerce.model.Option

fun OptionRequestDto.toEntity(): Option {
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
