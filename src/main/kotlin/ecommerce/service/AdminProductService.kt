package ecommerce.service

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.exception.DuplicateProductNameException
import ecommerce.exception.EntityNotFoundException
import ecommerce.mapper.toDto
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service
import java.net.URI

@Service
class AdminProductService(private val productRepository: ProductRepository) {
    fun getAllProducts(): List<ProductDTO> {
        val products = productRepository.findAll()
        return products.map { it.toDto() }
    }

    fun getProductById(id: Long): ProductDTO {
        val product =
            productRepository.findById(id)
                ?: throw EntityNotFoundException("Product with id: $id not found")
        return product.toDto()
    }

    fun createProduct(product: ProductDTO): URI {
        if (productRepository.existsByName(product.name)) {
            throw DuplicateProductNameException(product.name)
        }
        val id = productRepository.create(product)
        return URI.create("/products/$id")
    }

    fun updateProduct(
        id: Long,
        product: ProductDTO,
    ) {
        if (isDuplicateProductName(id, product.name)) {
            throw DuplicateProductNameException(product.name)
        }
        if (productRepository.update(id, product) == 0) {
            throw EntityNotFoundException("Product with id: $id not found")
        }
    }

    fun patchProduct(
        id: Long,
        patch: ProductPatchDTO,
    ) {
        val existing =
            productRepository.findById(id)
                ?: throw EntityNotFoundException("Product with id: $id not found")

        if (patch.name != null && isDuplicateProductName(id, patch.name)) {
            throw DuplicateProductNameException(patch.name)
        }

        val updatedProduct =
            existing.copy(
                name = patch.name ?: existing.name,
                description = patch.description ?: existing.description,
                price = patch.price ?: existing.price,
                imageUrl = patch.imageUrl ?: existing.imageUrl,
                quantity = patch.quantity ?: existing.quantity,
            )

        productRepository.update(id, updatedProduct.toDto())
    }

    fun deleteProduct(id: Long) {
        if (productRepository.deleteById(id) == 0) {
            productRepository.deleteById(id)
            throw EntityNotFoundException("Product with id: $id not found")
        }
    }

    private fun isDuplicateProductName(
        id: Long,
        name: String,
    ): Boolean {
        val oldProduct = productRepository.findByName(name)
        return oldProduct != null && oldProduct.id != id
    }
}
