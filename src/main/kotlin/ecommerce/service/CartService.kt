package ecommerce.service

import ecommerce.dto.cartProduct.CartProductResponseDTO
import ecommerce.entity.CartStatistics
import ecommerce.entity.Product
import ecommerce.entity.User
import ecommerce.enums.CartAction
import ecommerce.exception.EntityNotFoundException
import ecommerce.repository.CartStatisticsRepository
import ecommerce.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class CartService(
    private val productRepository: ProductRepository,
    private val cartStatisticsRepository: CartStatisticsRepository,
) {
    fun getCartProducts(user: User): List<CartProductResponseDTO> {
        val cart = user.cart ?: throw EntityNotFoundException("Cart not found")
        val products = cart.items
        return products.map {
            val product = it.product
            CartProductResponseDTO(
                productId = product.id,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = it.quantity,
            )
        }
    }

    @Transactional
    fun addProductToCart(
        member: User,
        productId: Long,
    ): Long {
        val cart = member.cart ?: throw EntityNotFoundException("Cart not found")
        val product = getValidProduct(productId)
        val addedItem = cart.addProduct(product)

        cartStatisticsRepository.save(
            CartStatistics(
                user = member,
                product = product,
                action = CartAction.ADD,
            ),
        )

        return addedItem.id
    }

    @Transactional
    fun removeProductFromCart(
        member: User,
        productId: Long,
    ) {
        val cart = member.cart ?: throw EntityNotFoundException("Cart not found")
        val product = getValidProduct(productId)

        cart.decrementProduct(product)
        cartStatisticsRepository.save(
            CartStatistics(
                user = member,
                product = product,
                action = CartAction.DELETE,
            ),
        )
    }

    private fun getValidProduct(productID: Long): Product {
        return productRepository.findById(productID).orElseThrow {
            EntityNotFoundException("Product not found")
        }
    }
}
