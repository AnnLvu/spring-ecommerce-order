package ecommerce.service

import ecommerce.dto.cartProduct.CartProductDTO
import ecommerce.dto.cartProduct.CartProductResponse
import ecommerce.enums.CartAction
import ecommerce.model.Cart
import ecommerce.model.CartStatistic
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.repository.CartStatisticRepository
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class CartService(
    private val productRepository: ProductRepository,
    private val cartStatisticRepository: CartStatisticRepository,
) {
    fun getCartProducts(member: User): CartProductResponse {
        val cart = getCart(member)
        val products = cart.items
        return CartProductResponse(
            products.map {
                val product = it.product
                CartProductDTO(
                    product.id,
                    product.name,
                    product.price,
                    product.imageUrl,
                    it.quantity,
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

        if (product.quantity == 0) throw EntityNotFoundException("Product not found")
        val addedItem = cart.addProduct(product)

        cartStatisticRepository.save(
            CartStatistic(
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
        cartStatisticRepository.save(
            CartStatistic(
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
                cartStatisticRepository.save(
                    CartStatistic(
                        member,
                        it.product,
                        CartAction.DELETE,
                    ),
                )
            }

        cartStatisticRepository.saveAll(stats)
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
