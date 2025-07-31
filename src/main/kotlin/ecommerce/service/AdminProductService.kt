package ecommerce.service

import ecommerce.controller.admin.AdminProductController.Companion.DEFAULT_PAGE
import ecommerce.controller.admin.AdminProductController.Companion.PER_PAGE
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.dto.products.ProductResponseDTO
import ecommerce.model.Product
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.DuplicateProductNameException
import ecommerce.utils.exception.EntityNotFoundException
import ecommerce.utils.extensions.getPaginatedDTOs
import ecommerce.utils.extensions.toProductDTO
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.net.URI

@Service
class AdminProductService(private val productRepository: ProductRepository) {
    fun getAllProducts(
        page: Int = DEFAULT_PAGE,
        perPage: Int = PER_PAGE,
    ): Page<ProductResponseDTO> {
        return productRepository.getPaginatedDTOs(page, perPage)
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
                    productDTO.name,
                    productDTO.price,
                    productDTO.imageUrl,
                    productDTO.quantity,
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

        if (isDuplicateProductName(id, productDTO.name)) {
            throw DuplicateProductNameException(productDTO.name)
        }

        product.name = productDTO.name
        product.price = productDTO.price
        product.quantity = productDTO.quantity
        product.imageUrl = productDTO.imageUrl

        productRepository.save(product)
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
