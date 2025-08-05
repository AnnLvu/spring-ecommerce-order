package ecommerce.dto.products

import ecommerce.dto.options.OptionRequestDto
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

class ProductPatchDto(
    @field:Length(min = 1, max = 15, message = "Product name must be no more than 15 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]{1,15}$",
        message = "Product name contains invalid characters",
    )
    val name: String? = null,
    val optionsList: MutableList<OptionRequestDto>? = null,
)
