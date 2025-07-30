package ecommerce.service

import ecommerce.dto.products.ProductResponseDTO
import ecommerce.repository.ProductRepository
import ecommerce.utils.toProductDTO
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(): List<ProductResponseDTO> {
        val products = productRepository.findAll()
        return products.map { it.toProductDTO() }
    }
}
