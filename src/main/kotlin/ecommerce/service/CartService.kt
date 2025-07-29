package ecommerce.service

import ecommerce.dto.cartProduct.CartProductResponseDTO
import ecommerce.entity.Cart
import ecommerce.entity.CartProductResponse
import ecommerce.enums.CartAction
import ecommerce.exception.CartOperationException
import ecommerce.exception.EntityNotFoundException
import ecommerce.repository.CartProductRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.CartStatisticsRepository
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartProductRepository: CartProductRepository,
    private val productRepository: ProductRepository,
    private val cartStatisticsRepository: CartStatisticsRepository,
) {
    fun getCartProducts(userID: Long?): List<CartProductResponseDTO> {
        val cart = getUserCart(userID)
        val products = cartProductRepository.getCartProducts(cart.id)
        return products.map { it.toDTO() }
    }

    fun addProductToCart(
        userID: Long?,
        productID: Long,
    ): Long {
        val cart = getUserCart(userID)
        checkValidProduct(productID)

        val cartProduct = cartProductRepository.findCartProduct(cart.id, productID)

        return try {
            val id =
                if (cartProduct == null) {
                    cartProductRepository.addProduct(cart.id, productID)
                } else {
                    cartProductRepository.updateProductQuantity(cartProduct.id, cartProduct.quantity + 1)
                    cartProduct.id
                }

            cartStatisticsRepository.create(userID, productID, CartAction.ADD)

            id
        } catch (_: Exception) {
            throw CartOperationException("Failed to add product to cart")
        }
    }

    fun removeProductFromCart(
        userID: Long?,
        productID: Long,
    ) {
        val cart = getUserCart(userID)
        checkValidProduct(productID)

        val cartProduct =
            cartProductRepository.findCartProduct(cart.id, productID)
                ?: throw EntityNotFoundException("Product not in cart")

        try {
            if (cartProduct.quantity == 1) {
                cartProductRepository.removeProduct(cart.id, productID)
            } else {
                cartProductRepository.updateProductQuantity(cartProduct.id, cartProduct.quantity - 1)
            }
        } catch (_: Exception) {
            throw CartOperationException("Failed to remove product from cart")
        }
        cartStatisticsRepository.create(userID, productID, CartAction.DELETE)
    }

    private fun getUserCart(userID: Long?): Cart {
        return cartRepository.findMembersCart(userID)
            ?: throw EntityNotFoundException("Cart not found")
    }

    private fun checkValidProduct(productID: Long) {
        productRepository.findById(productID) ?: throw EntityNotFoundException("Product not found")
    }

    private fun CartProductResponse.toDTO(): CartProductResponseDTO {
        return CartProductResponseDTO(
            this.productId,
            this.name,
            this.description,
            this.price,
            this.imageUrl,
            this.quantity,
        )
    }
}
