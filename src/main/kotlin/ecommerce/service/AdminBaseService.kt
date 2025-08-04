import ecommerce.dto.options.OptionRequestDto
import ecommerce.exception.EntityNotFoundException
import ecommerce.extensions.toEntity
import ecommerce.model.Option
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository

abstract class AdminBaseService(
    protected val productRepository: ProductRepository,
    protected val optionRepository: OptionRepository,
) {
    protected fun getOptionMutableList(option: MutableList<OptionRequestDto>): MutableList<Option> {
        return option.map { it.toEntity() }.toMutableList()
    }

    protected fun isDuplicateProductName(
        id: Long,
        name: String,
    ): Boolean {
        val oldProduct = productRepository.findByName(name).orElse(null)
        return oldProduct != null && oldProduct.id != id
    }

    protected fun getValidProduct(productId: Long): Product {
        return productRepository.findById(productId).orElseThrow { EntityNotFoundException("Product with id $productId not found") }
    }

    protected fun findOption(
        product: Product,
        optionId: Long,
    ): Option {
        return product.options.find { it.id == optionId } ?: throw EntityNotFoundException("Option not found")
    }
}
