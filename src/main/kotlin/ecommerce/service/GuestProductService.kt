package ecommerce.service

import ecommerce.dto.products.ProductResponseDto
import ecommerce.extensions.getPaginatedDtos
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(
        page: Int,
        perPage: Int,
    ): Page<ProductResponseDto> {
        return productRepository.getPaginatedDtos(page, perPage)
    }
}
