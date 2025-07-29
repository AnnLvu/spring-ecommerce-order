package ecommerce.service

import ecommerce.dto.products.ProductDTO
import ecommerce.mapper.toDto
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class GuestProductService(
    private val productRepository: ProductRepository,
) {
    fun getListProducts(): List<ProductDTO> {
        val products = productRepository.findAll()
        return products.map { it.toDto() }
    }
}
