package ecommerce.service

import AdminBaseService
import ecommerce.config.PaginationConstants.DEFAULT_PAGE
import ecommerce.config.PaginationConstants.PER_PAGE
import ecommerce.dto.products.ProductPatchDto
import ecommerce.dto.products.ProductRequestDto
import ecommerce.dto.products.ProductResponseDto
import ecommerce.exception.DuplicateProductNameException
import ecommerce.extensions.getPaginatedDtos
import ecommerce.extensions.toProductDto
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
@Transactional
class AdminProductService(
    productRepository: ProductRepository,
    optionRepository: OptionRepository,
) : AdminBaseService(productRepository, optionRepository) {
    fun getAllProducts(
        page: Int = DEFAULT_PAGE,
        perPage: Int = PER_PAGE,
    ): Page<ProductResponseDto> {
        return productRepository.getPaginatedDtos(page, perPage)
    }

    fun getProductById(id: Long): ProductResponseDto {
        val product = getValidProduct(id)
        return product.toProductDto()
    }

    fun createProduct(productDto: ProductRequestDto): URI {
        if (productRepository.existsByName(productDto.name)) {
            throw DuplicateProductNameException(productDto.name)
        }

        val product =
            productRepository.save(
                Product(
                    productDto.name,
                    getOptionMutableList(productDto.optionsList),
                ),
            )
        return URI.create("/products/${product.id}")
    }

    fun updateProduct(
        id: Long,
        productDto: ProductRequestDto,
    ) {
        val product = getValidProduct(id)

        if (isDuplicateProductName(id, productDto.name)) {
            throw DuplicateProductNameException(productDto.name)
        }

        product.options.clear()
        product.name = productDto.name
        val newOptions = getOptionMutableList(productDto.optionsList)
        product.options.addAll(newOptions)
    }

    fun patchProduct(
        id: Long,
        productPatchDto: ProductPatchDto,
    ) {
        val existingProduct = getValidProduct(id)

        productPatchDto.name?.let { newName ->
            if (newName != existingProduct.name && isDuplicateProductName(id, newName)) {
                throw DuplicateProductNameException(newName)
            }
            existingProduct.name = newName
        }

        productPatchDto.optionsList?.let {
            optionRepository.deleteAllById(existingProduct.options.map { it.id })
            existingProduct.options = getOptionMutableList(productPatchDto.optionsList)
        }
    }

    fun deleteProduct(id: Long) {
        val product = getValidProduct(id)
        productRepository.delete(product)
    }
}
