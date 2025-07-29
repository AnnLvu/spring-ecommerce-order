package ecommerce.dto.products

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Length

data class ProductDTO(
    val id: Long? = null,
    @field:Length(min = 3, max = 15, message = "name should be between 3 and 15")
    @field:Pattern(
        regexp = "^[a-zA-Z1-9()\\[\\]+\\-&/_]+$",
        message = "Product name contains invalid characters",
    )
    val name: String,
    @field:Length(min = 3, message = "Description must be greater than 3 characters")
    val description: String,
    @field:Positive(message = "Product price must be greater than 0")
    val price: Double,
    @field:NotBlank(message = "Image URL cannot be blank")
    @field:Pattern(
        regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)$",
        message = "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp",
    )
    val imageUrl: String,
    @field:Min(value = 0, message = "Quantity cannot be negative")
    val quantity: Int = 1,
)
