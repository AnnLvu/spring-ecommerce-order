package ecommerce.service

import ecommerce.controller.admin.AdminProductController.Companion.DEFAULT_PAGE
import ecommerce.controller.admin.AdminProductController.Companion.PER_PAGE
import ecommerce.dto.products.OptionDTO
import ecommerce.dto.products.OptionPatchDTO
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.dto.products.ProductResponseDTO
import ecommerce.model.Option
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.DuplicateProductNameException
import ecommerce.utils.exception.EntityNotFoundException
import ecommerce.utils.extensions.getPaginatedDTOs
import ecommerce.utils.extensions.toEntity
import ecommerce.utils.extensions.toProductDTO
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
                    getOptionMutableList(productDTO.optionsList),
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

        product.options.clear()

        product.name = productDTO.name
        val newOptions = getOptionMutableList(productDTO.optionsList)
        product.options.addAll(newOptions)
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

        productPatchDTO.optionsList?.let {
            optionRepository.deleteAllById(existingProduct.options.map { it.id })
            existingProduct.options = getOptionMutableList(productPatchDTO.optionsList)
        }
    }

    fun deleteProduct(id: Long) {
        val product = getValidProduct(id)

        productRepository.delete(product)
    }

    fun getProductOptions(productId: Long): ProductResponseDTO {
        val product = getValidProduct(productId)
        return product.toProductDTO()
    }

    fun createOption(
        productId: Long,
        optionDTO: OptionDTO,
    ): URI {
        val product = getValidProduct(productId)

        if (product.options.find { it.name == optionDTO.name } != null) {
            throw DuplicateProductNameException("Duplicate option not accepted")
        }

        val newOption = optionRepository.save(optionDTO.toEntity())
        product.options.add(newOption)

        return URI.create("/products/${product.id}/options/${newOption.id}")
    }

    fun updateOption(
        productId: Long,
        optionId: Long,
        optionDTO: OptionDTO,
    ) {
        val product = getValidProduct(productId)
        val option = findOption(product, optionId)

        option.name = optionDTO.name
        option.price = optionDTO.price
        option.quantity = optionDTO.quantity
        option.imageUrl = optionDTO.imageUrl
    }

    fun patchOption(
        productId: Long,
        optionId: Long,
        optionPatchDTO: OptionPatchDTO,
    ) {
        val product = getValidProduct(productId)
        val option = findOption(product, optionId)

        optionPatchDTO.name?.let { option.name = it }
        optionPatchDTO.price?.let { option.price = it }
        optionPatchDTO.quantity?.let { option.quantity = it }
        optionPatchDTO.imageUrl?.let { option.imageUrl = it }
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

    private fun getOptionMutableList(option: MutableList<OptionDTO>): MutableList<Option> {
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
