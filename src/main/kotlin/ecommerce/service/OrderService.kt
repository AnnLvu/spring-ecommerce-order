package ecommerce.service

import ecommerce.dto.options.OptionQuantity
import ecommerce.dto.order.OrderResponseDto
import ecommerce.dto.order.PlaceOrderRequest
import ecommerce.dto.order.PlaceOrderResponse
import ecommerce.dto.stripe.PaymentRequest
import ecommerce.dto.stripe.PaymentResponse
import ecommerce.exception.PaymentException
import ecommerce.exception.StripePaymentException
import ecommerce.extensions.OrderMapper
import ecommerce.extensions.toDto
import ecommerce.extensions.toUserFriendlyMessage
import ecommerce.model.CartProduct
import ecommerce.model.User
import ecommerce.repository.CartProductRepository
import ecommerce.repository.OptionRepository
import ecommerce.repository.OrderRepository
import ecommerce.repository.UserRepository
import ecommerce.stripe.StripeClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val userRepository: UserRepository,
    private val optionRepository: OptionRepository,
    private val cartProductRepository: CartProductRepository,
    private val orderRepository: OrderRepository,
    private val stripeClient: StripeClient,
) {
    @Transactional
    fun placeOrder(
        userId: Long,
        placeOrderRequest: PlaceOrderRequest,
    ): PlaceOrderResponse {
        val user = loadUserById(userId)
        val cartProducts = loadCartProductsForUser(user)
        val optionQuantityList = validateStockAndPrepareLineItems(cartProducts)
        val totalAmount = calculateTotalAmount(optionQuantityList)

        var order = OrderMapper.newPending(user, totalAmount, placeOrderRequest)
        order = orderRepository.save(order)

        val payment: PaymentResponse =
            try {
                createStripeCheckoutSession(totalAmount, placeOrderRequest)
            } catch (ex: StripePaymentException) {
                val raw = ex.declineCode ?: ex.code ?: "payment_error"
                val msg = raw.toUserFriendlyMessage()
                order = OrderMapper.applyFailed(order, msg)
                orderRepository.save(order)
                throw PaymentException(msg)
            } catch (ex: Exception) {
                val msg = "Unable to process the payment: ${ex.message ?: "technical error"}"
                order = OrderMapper.applyFailed(order, msg)
                orderRepository.save(order)
                throw PaymentException(msg)
            }

        if (payment.status != "succeeded") {
            val raw = payment.declineCode ?: payment.status
            val msg = raw.toUserFriendlyMessage()
            order = OrderMapper.applyFailed(order, msg, payment.id)
            orderRepository.save(order)
            throw PaymentException(msg)
        }

        deductStockAndClearCart(user, optionQuantityList)

        order = OrderMapper.applyPaid(order, payment.id, optionQuantityList)
        order = orderRepository.save(order)

        return PlaceOrderResponse(order.id, order.stripeSessionId)
    }

    @Transactional(readOnly = true)
    fun listOrders(userId: Long): List<OrderResponseDto> {
        val user =
            userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("Invalid user ID: $userId") }

        return orderRepository.findAllByUserOrderByCreatedAtDesc(user)
            .map { it.toDto() }
    }

    private fun loadUserById(userId: Long) =
        userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Invalid user ID: $userId") }

    private fun loadCartProductsForUser(user: User): List<CartProduct> {
        val cart = user.cart ?: throw IllegalArgumentException("Cart not found for user ${user.id}")
        val cartProducts = cartProductRepository.findByCart(cart)
        if (cartProducts.isEmpty()) {
            throw IllegalArgumentException("Cart is empty for user ${user.id}")
        }
        return cartProducts
    }

    private fun validateStockAndPrepareLineItems(cartProducts: List<CartProduct>): List<OptionQuantity> {
        return cartProducts.map { cartProduct ->
            val productOption =
                optionRepository.findById(cartProduct.option.id)
                    .orElseThrow { IllegalArgumentException("Invalid option ID: ${cartProduct.option.id}") }
            if (productOption.quantity < cartProduct.quantity) {
                throw IllegalArgumentException("Insufficient stock for option ID: ${productOption.id}")
            }
            OptionQuantity(productOption, cartProduct.quantity)
        }
    }

    private fun calculateTotalAmount(optionQuantityList: List<OptionQuantity>): Double {
        return optionQuantityList.sumOf { (productOption, quantity) ->
            productOption.price * quantity
        }
    }

    private fun createStripeCheckoutSession(
        totalAmount: Double,
        placeOrderRequest: PlaceOrderRequest,
    ): PaymentResponse {
        return stripeClient.createCheckoutSession(
            PaymentRequest(
                totalAmount,
                placeOrderRequest.currency,
                placeOrderRequest.paymentMethodId,
            ),
        )
    }

    private fun deductStockAndClearCart(
        user: User,
        optionQuantityList: List<OptionQuantity>,
    ) {
        optionQuantityList.forEach { (productOption, quantity) ->
            productOption.quantity -= quantity
            optionRepository.save(productOption)
            cartProductRepository.deleteByCartAndOption(user.cart!!, productOption)
        }
    }
}
