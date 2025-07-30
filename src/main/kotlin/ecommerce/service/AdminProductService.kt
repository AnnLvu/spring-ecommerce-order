package ecommerce.service

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.dto.products.ProductResponseDTO
import ecommerce.exception.DuplicateProductNameException
import ecommerce.exception.EntityNotFoundException
import ecommerce.entity.Product
import ecommerce.repository.ProductRepository
import ecommerce.utils.toProductDTO
import org.springframework.stereotype.Service
import java.net.URI

@Service
class AdminProductService(private val productRepository: ProductRepository) {
    fun getAllProducts(): List<ProductResponseDTO> {
        val products = productRepository.findAll()
        return products.map { it.toProductDTO() }
    }

    fun getProductById(id: Long): ProductResponseDTO {
        val product = getValidProduct(id)
        return product.toProductDTO()
    }

    fun createProduct(productDTO: ProductDTO): URI {
        if (productRepository.existsByName(productDTO.name)) {
            throw DuplicateProductNameException(productDTO.name)
        }

        val product =
            productRepository.save(
                Product(
                    name = productDTO.name,
                    price = productDTO.price,
                    quantity = productDTO.quantity,
                    imageUrl = productDTO.imageUrl,
                ),
            )
        return URI.create("/products/${product.id}")
    }

    fun updateProduct(
        id: Long,
        productDTO: ProductDTO,
    ) {
        val product =
            getValidProduct(id)

        product.name = productDTO.name
        product.price = productDTO.price
        product.quantity = productDTO.quantity
        product.imageUrl = productDTO.imageUrl

        try {
            productRepository.save(product)
        } catch (_: Exception) {
            throw DuplicateProductNameException(productDTO.name)
        }
    }

    fun patchProduct(
        id: Long,
        productPatchDTO: ProductPatchDTO,
    ) {
        val existingProduct = getValidProduct(id)

        productPatchDTO.name?.let { newName ->
            if (newName != existingProduct.name && isDuplicateProductName(id, newName)) {
                throw DuplicateProductNameException(newName)
            }
            existingProduct.name = newName
        }

        productPatchDTO.price?.let {
            existingProduct.price = it
        }

        productPatchDTO.imageUrl?.let {
            existingProduct.imageUrl = it
        }

        productPatchDTO.quantity?.let {
            existingProduct.quantity = it
        }

        productRepository.save(existingProduct)
    }

    private fun isDuplicateProductName(
        id: Long,
        name: String,
    ): Boolean {
        val oldProduct = productRepository.findByName(name).orElse(null)
        return oldProduct != null && oldProduct.id != id
    }

    private fun getValidProduct(productId: Long): Product {
        return productRepository.findById(productId).orElseThrow { EntityNotFoundException("Product with id $productId not found") }
    }

    fun deleteProduct(id: Long) {
        val product = getValidProduct(id)
        productRepository.delete(product)
    }
}
