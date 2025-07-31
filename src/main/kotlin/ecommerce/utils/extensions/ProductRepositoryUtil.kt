package ecommerce.utils.extensions

import ecommerce.dto.products.ProductListResponse
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.PageRequest

fun ProductRepository.getPaginatedDTOs(
    page: Int,
    perPage: Int,
): ProductListResponse {
    require(page - 1 >= 0) { IllegalArgumentException("page must be > 0") }
    require(perPage > 1) { IllegalArgumentException("perPage must be > 1") }

    val pageable = PageRequest.of(page - 1, perPage)
    val products = findAll(pageable)
    return ProductListResponse(products.map { it.toProductDTO() }.toList())
}
