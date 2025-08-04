package ecommerce.service

import AdminBaseService
import ecommerce.dto.options.OptionPatchDto
import ecommerce.dto.options.OptionRequestDto
import ecommerce.dto.products.ProductResponseDto
import ecommerce.exception.DuplicateProductNameException
import ecommerce.extensions.toEntity
import ecommerce.extensions.toProductDto
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
@Transactional
class AdminOptionService(
    productRepository: ProductRepository,
    optionRepository: OptionRepository,
) : AdminBaseService(productRepository, optionRepository) {

    fun getProductOptions(productId: Long): ProductResponseDto {
        val product = getValidProduct(productId)
        return product.toProductDto()
    }

    fun createOption(
        productId: Long,
        optionDto: OptionRequestDto,
    ): URI {
        val product = getValidProduct(productId)

        if (product.options.any { it.name == optionDto.name }) {
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
}
