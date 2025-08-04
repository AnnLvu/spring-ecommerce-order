package ecommerce.service

import ecommerce.dto.cartProduct.CartProductDto
import ecommerce.dto.cartProduct.CartProductResponse
import ecommerce.enums.CartAction
import ecommerce.model.Cart
import ecommerce.model.CartProduct
import ecommerce.model.CartStatistic
import ecommerce.model.Option
import ecommerce.model.User
import ecommerce.repository.CartStatisticRepository
import ecommerce.repository.OptionRepository
import ecommerce.utils.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartStatisticRepository: CartStatisticRepository,
    private val optionRepository: OptionRepository,
) {
    fun getCartProducts(member: User): CartProductResponse {
        val cart = getCart(member)
        return CartProductResponse(
            cart.items.map { it.toDto() },
        )
    }

    @Transactional
    fun addProductToCart(
        member: User,
        optionId: Long,
    ): Long {
        val cart = getCart(member)
        val option = getValidProductOption(optionId)

        val addedItem = cart.addProduct(option)

        cartStatisticRepository.save(
            CartStatistic(
                member,
                option,
                CartAction.ADD,
            ),
        )

        return addedItem.id
    }

    @Transactional
    fun removeProductFromCart(
        member: User,
        optionId: Long,
    ) {
        val cart = getCart(member)
        val option = getValidProductOption(optionId)

        cart.decrementProduct(option)

        cartStatisticRepository.save(
            CartStatistic(
                member,
                option,
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
                CartStatistic(
                    member,
                    it.option,
                    CartAction.DELETE,
                )
            }

        cartStatisticRepository.saveAll(stats)
        cart.clear()
    }

    private fun getCart(member: User): Cart {
        return member.cart ?: throw EntityNotFoundException("Cart not found")
    }

    private fun getValidProductOption(optionId: Long): Option {
        return optionRepository.findById(optionId)
            .orElseThrow { EntityNotFoundException("Product option not found") }
    }

    private fun CartProduct.toDto(): CartProductDto {
        return CartProductDto(
            option.id,
            option.name,
            option.price,
            option.imageUrl,
            quantity,
        )
    }
}
