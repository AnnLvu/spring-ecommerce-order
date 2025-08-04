package ecommerce.service

import ecommerce.config.PaginationConstants.DEFAULT_PAGE
import ecommerce.config.PaginationConstants.PER_PAGE
import ecommerce.dto.products.OptionRequestDto
import ecommerce.dto.products.OptionPatchDto
import ecommerce.dto.products.ProductRequestDto
import ecommerce.dto.products.ProductPatchDto
import ecommerce.dto.products.ProductResponseDto
import ecommerce.model.Option
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import ecommerce.exception.DuplicateProductNameException
import ecommerce.exception.EntityNotFoundException
import ecommerce.extensions.getPaginatedDtos
import ecommerce.extensions.toEntity
import ecommerce.extensions.toProductDto
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.net.URI

@Service
@Transactional
class AdminProductService(
    private val productRepository: ProductRepository,
    private val optionRepository: OptionRepository,
) {
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
        val product =
            getValidProduct(id)

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

    fun getProductOptions(productId: Long): ProductResponseDto {
        val product = getValidProduct(productId)
        return product.toProductDto()
    }

    fun createOption(
        productId: Long,
        optionDto: OptionRequestDto,
    ): URI {
        val product = getValidProduct(productId)

        if (product.options.find { it.name == optionDto.name } != null) {
            throw DuplicateProductNameException("Duplicate option not accepted")
        }

        val newOption = optionRepository.save(optionDto.toEntity())
        product.options.add(newOption)

        return URI.create("/products/${product.id}/options/${newOption.id}")
    }

    fun updateOption(
        productId: Long,
        optionId: Long,
        optionDto: OptionRequestDto,
    ) {
        val product = getValidProduct(productId)
        val option = findOption(product, optionId)

        option.name = optionDto.name
        option.price = optionDto.price
        option.quantity = optionDto.quantity
        option.imageUrl = optionDto.imageUrl
    }

    fun patchOption(
        productId: Long,
        optionId: Long,
        optionPatchDto: OptionPatchDto,
    ) {
        val product = getValidProduct(productId)
        val option = findOption(product, optionId)

        optionPatchDto.name?.let { option.name = it }
        optionPatchDto.price?.let { option.price = it }
        optionPatchDto.quantity?.let { option.quantity = it }
        optionPatchDto.imageUrl?.let { option.imageUrl = it }
    }

    fun deleteOption(
        productId: Long,
        optionId: Long,
    ) {
        val product = getValidProduct(productId)
        val option = findOption(product, optionId)
        product.options.remove(option)

        optionRepository.delete(option)
    }

    private fun isDuplicateProductName(
        id: Long,
        name: String,
    ): Boolean {
        val oldProduct = productRepository.findByName(name).orElse(null)
        return oldProduct != null && oldProduct.id != id
    }

    private fun getOptionMutableList(option: MutableList<OptionRequestDto>): MutableList<Option> {
        return option.map { it.toEntity() }.toMutableList()
    }

    private fun findOption(
        product: Product,
        optionId: Long,
    ): Option {
        return product.options.find { it.id == optionId } ?: throw EntityNotFoundException("Option not found")
    }

    private fun getValidProduct(productId: Long): Product {
        return productRepository.findById(productId).orElseThrow { EntityNotFoundException("Product with id $productId not found") }
    }
}
