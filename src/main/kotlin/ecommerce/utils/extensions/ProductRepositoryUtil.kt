package ecommerce.utils.extensions

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

fun ProductRepository.getPaginatedDTOs(
    page: Int,
    perPage: Int,
): Page<ProductResponseDTO> {
    require(page - 1 >= 0) { IllegalArgumentException("page must be > 0") }
    require(perPage > 1) { IllegalArgumentException("perPage must be > 1") }

    val pageable = PageRequest.of(page - 1, perPage)
    val products = findAll(pageable)
    return products.map { it.toProductDTO() }
}
