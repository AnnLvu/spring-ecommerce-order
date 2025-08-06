package ecommerce.dto.options

import ecommerce.model.Option

data class OptionQuantity(
    val option: Option,
    val quantity: Int
)
