package ecommerce.service

import ecommerce.dto.options.OptionQuantity
import ecommerce.dto.order.PlaceOrderRequest
import ecommerce.dto.order.PlaceOrderResponse
import ecommerce.dto.stripe.PaymentRequest
import ecommerce.exception.PaymentException
import ecommerce.model.CartProduct
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.User
import ecommerce.repository.CartProductRepository
import ecommerce.repository.OptionRepository
import ecommerce.repository.OrderRepository
import ecommerce.repository.UserRepository
import ecommerce.stripe.StripeClient
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val userRepository: UserRepository,
    private val optionRepository: OptionRepository,
    private val cartProductRepository: CartProductRepository,
    private val orderRepository: OrderRepository,
    private val stripeClient: StripeClient
) {
    @Transactional
    fun placeOrder(userId: Long, placeOrderRequest: PlaceOrderRequest): PlaceOrderResponse {
        val user = loadUserById(userId)
        val cartProducts = loadCartProductsForUser(user)
        val optionQuantityList = validateStockAndPrepareLineItems(cartProducts)
        val totalAmount = calculateTotalAmount(optionQuantityList)
        val checkoutSessionId = createStripeCheckoutSession(totalAmount, placeOrderRequest)
        deductStockAndClearCart(user, optionQuantityList)
        val savedOrder = buildAndSaveOrder(user, checkoutSessionId, totalAmount, optionQuantityList)
        return PlaceOrderResponse(
            savedOrder.id,
            checkoutSessionId
        )
    }

    private fun loadUserById(userId: Long) =
        userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Invalid user ID: $userId") }

    private fun loadCartProductsForUser(user: User): List<CartProduct> {
        val cart = user.cart
            ?: throw IllegalArgumentException("Cart not found for user ${user.id}")
        val cartProducts = cartProductRepository.findByCart(cart)
        if (cartProducts.isEmpty()) {
            throw IllegalArgumentException("Cart is empty for user ${user.id}")
        }
        return cartProducts
    }

    private fun validateStockAndPrepareLineItems(
        cartProducts: List<CartProduct>
    ): List<OptionQuantity> {
        return cartProducts.map { cartProduct ->
            val productOption = optionRepository.findById(cartProduct.option.id)
                .orElseThrow { IllegalArgumentException("Invalid option ID: ${cartProduct.option.id}") }
            if (productOption.quantity < cartProduct.quantity) {
                throw IllegalArgumentException("Insufficient stock for option ID: ${productOption.id}")
            }
            OptionQuantity(productOption, cartProduct.quantity)
        }
    }

    private fun calculateTotalAmount(
        optionQuantityList: List<OptionQuantity>
    ): Double {
        return optionQuantityList.sumOf { (productOption, quantity) ->
            productOption.price * quantity
        }
    }

    private fun createStripeCheckoutSession(
        totalAmount: Double,
        placeOrderRequest: PlaceOrderRequest
    ): String {
        val paymentResponse = try {
            stripeClient.createCheckoutSession(
                PaymentRequest(
                    amount = totalAmount,
                    currency = placeOrderRequest.currency,
                    paymentMethod = placeOrderRequest.paymentMethod
                )
            )
        } catch (exception: Exception) {
            throw PaymentException(exception.message ?: "Payment processing failed")
        }
        return paymentResponse.id.toString()
    }

    private fun deductStockAndClearCart(
        user: User,
        optionQuantityList: List<OptionQuantity>
    ) {
        optionQuantityList.forEach { (productOption, quantity) ->
            productOption.quantity -= quantity
            optionRepository.save(productOption)
            cartProductRepository.deleteByCartAndOption(user.cart!!, productOption)
        }
    }

    private fun buildAndSaveOrder(
        user: User,
        checkoutSessionId: String,
        totalAmount: Double,
        optionQuantityList: List<OptionQuantity>
    ): Order {
        val order = Order(
            user,
            checkoutSessionId,
            totalAmount
        )
        optionQuantityList.forEach { (productOption, quantity) ->
            val orderItem = OrderItem(
                order,
                productOption,
                quantity
            )
            order.items.add(orderItem)
        }
        return orderRepository.save(order)
    }
}