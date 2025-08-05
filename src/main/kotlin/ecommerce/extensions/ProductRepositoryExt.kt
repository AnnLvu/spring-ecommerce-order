package ecommerce.extensions

import ecommerce.dto.products.ProductResponseDto
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

fun ProductRepository.getPaginatedDtos(
    page: Int,
    perPage: Int,
): Page<ProductResponseDto> {
    require(page - 1 >= 0) { IllegalArgumentException("page must be > 0") }
    require(perPage > 1) { IllegalArgumentException("perPage must be > 1") }

    val pageable = PageRequest.of(page - 1, perPage)
    val products = findAll(pageable)
    return products.map { it.toProductDto() }
}
