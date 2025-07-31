package ecommerce.service

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.model.Product
import ecommerce.repository.ProductRepository
import ecommerce.utils.extensions.getPaginatedDTOs
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(
        page: Int,
        perPage: Int,
    ): Page<ProductResponseDTO> {
        return productRepository.getPaginatedDTOs(page, perPage)
    }
}
