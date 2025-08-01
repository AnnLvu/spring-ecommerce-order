package ecommerce.dto.products

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

class ProductPatchDTO(
    @field:Length(min = 1, max = 15, message = "Product name must be no more than 15 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]{1,15}$",
        message = "Product name contains invalid characters",
    )
    val name: String? = null,
    @field:Pattern(
        regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)$",
        message = "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp",
    )
    val imageUrl: String? = null,
    @Valid
    val optionsList: MutableList<OptionDTO>? = null,
)
