package ecommerce.dto.products

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

data class ProductDto(
    @field:Length(min = 3, max = 15, message = "name should be between 3 and 15")
    @field:Pattern(
        regexp = "^[a-zA-Z1-9()\\[\\]+\\-&/_]+$",
        message = "Product name contains invalid characters",
    )
    val name: String,
    @field:Size(min = 1, message = "At least one option required")
    val optionsList: MutableList<OptionDto>,
)
