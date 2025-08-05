package ecommerce.dto.options

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

class OptionRequestDto(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]+$")
    @field:Size(min = 1, max = NAME_MAX_LENGTH)
    val name: String,
    @field:Positive(message = "Options price should be greater than 0")
    val price: Double,
    @field:Range(min = MIN_QUANTITY, max = MAX_QUANTITY)
    val quantity: Int,
    @field:Pattern(
        regexp = "^https?://.*\\.(png|jpg|jpeg|gif|webp)$",
        message = "Image must be a valid URL ending in .png, .jpg, .jpeg, .gif, or .webp",
    )
    val imageUrl: String,
) {
    companion object {
        private const val NAME_MAX_LENGTH = 50
        private const val MIN_QUANTITY = 1L
        private const val MAX_QUANTITY = 100_000_000L
    }
}
