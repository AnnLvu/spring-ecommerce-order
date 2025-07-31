package ecommerce.service

import ecommerce.dto.products.ProductListResponse
import ecommerce.repository.ProductRepository
import ecommerce.utils.extensions.getPaginatedDTOs
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(
        page: Int,
        perPage: Int,
    ): ProductListResponse {
        return productRepository.getPaginatedDTOs(page, perPage)
    }
}
