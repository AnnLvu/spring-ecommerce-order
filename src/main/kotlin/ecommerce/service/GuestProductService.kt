package ecommerce.service

import ecommerce.dto.products.ProductListResponse
import ecommerce.repository.ProductRepository
import ecommerce.utils.toProductDTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(
        page: Int,
        perPage: Int,
    ): ProductListResponse {
        require(perPage - 1 >= 0) { IllegalArgumentException("page must be > 0") }
        require(perPage > 1) { IllegalArgumentException("perPage must be > 1") }

        val pageable = PageRequest.of(page - 1, perPage)
        val products = productRepository.findAll(pageable)
        return ProductListResponse(products.map { it.toProductDTO() }.toList())
    }
}
