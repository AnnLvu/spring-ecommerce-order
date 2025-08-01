package ecommerce.dto.products

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

data class ProductDTO(
    @field:Length(min = 3, max = 15, message = "name should be between 3 and 15")
    @field:Pattern(
        regexp = "^[a-zA-Z1-9()\\[\\]+\\-&/_]+$",
        message = "Product name contains invalid characters",
    )
    val name: String,
    @field:NotBlank(message = "Image URL cannot be blank")
    @field:Pattern(
        regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)$",
        message = "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp",
    )
    val imageUrl: String,
    @field:Size(min = 1, message = "At least one option required")
    @Valid
    val optionsList: MutableList<OptionDTO>,
)
