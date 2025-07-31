package ecommerce.service

import ecommerce.dto.cartProduct.CartProductDTO
import ecommerce.dto.cartProduct.CartProductResponse
import ecommerce.enums.CartAction
import ecommerce.model.Cart
import ecommerce.model.CartStatistics
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.repository.CartStatisticsRepository
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class CartService(
    private val productRepository: ProductRepository,
    private val cartStatisticsRepository: CartStatisticsRepository,
) {
    fun getCartProducts(member: User): CartProductResponse {
        val cart = getCart(member)
        val products = cart.items
        return CartProductResponse(
            products.map {
                val product = it.product
                CartProductDTO(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    quantity = it.quantity,
                )
            },
        )
    }

    @Transactional
    fun addProductToCart(
        member: User,
        productId: Long,
    ): Long {
        val cart = getCart(member)
        val product = getValidProduct(productId)
        val addedItem = cart.addProduct(product)

        cartStatisticsRepository.save(
            CartStatistics(
                member,
                product,
                CartAction.ADD,
            ),
        )

        return addedItem.id
    }

    @Transactional
    fun removeProductFromCart(
        member: User,
        productId: Long,
    ) {
        val cart = getCart(member)
        val product = getValidProduct(productId)

        cart.decrementProduct(product)
        cartStatisticsRepository.save(
            CartStatistics(
                member,
                product,
                CartAction.DELETE,
            ),
        )
    }

    @Transactional
    fun clearCart(member: User) {
        val cart = getCart(member)

        if (cart.items.isEmpty()) {
            throw EntityNotFoundException("No items found")
        }

        val stats =
            cart.items.map {
                cartStatisticsRepository.save(
                    CartStatistics(
                        member,
                        it.product,
                        CartAction.DELETE,
                    ),
                )
            }

        cartStatisticsRepository.saveAll(stats)
        cart.clear()
    }

    private fun getCart(member: User): Cart {
        return member.cart ?: throw EntityNotFoundException("Cart not found")
    }

    private fun getValidProduct(productID: Long): Product {
        return productRepository.findById(productID).orElseThrow {
            EntityNotFoundException("Product not found")
        }
    }
}
