package ecommerce.dto.products

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Length

class ProductPatchDTO(
    @field:Length(min = 1, max = 15, message = "Product name must be no more than 15 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]{1,15}$",
        message = "Product name contains invalid characters",
    )
    val name: String? = null,
    @field:Length(min = 3, message = "Description must be between 3 and 255 characters")
    val description: String? = null,
    @field:Positive(message = "Product price must be greater than 0")
    val price: Double? = null,
    @field:Pattern(
        regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)$",
        message = "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp",
    )
    val imageUrl: String? = null,
    @field:Min(value = 0, message = "Quantity cannot be negative")
    val quantity: Int? = null,
)
