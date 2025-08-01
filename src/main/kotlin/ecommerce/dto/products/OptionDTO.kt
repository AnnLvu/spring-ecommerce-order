package ecommerce.dto.products

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

class OptionDTO(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]+$")
    @field:Size(min = 1, max = NAME_MAX_LENGTH)
    val name: String,
    @field:NotBlank
    @field:Positive
    val price: Double,
    @field:NotBlank
    @field:Range(min = MIN_QUANTITY, max = MAX_QUANTITY)
    val quantity: Int,
) {
    companion object {
        private const val NAME_MAX_LENGTH = 50
        private const val MIN_QUANTITY = 1L
        private const val MAX_QUANTITY = 100_000_000L
    }
}
