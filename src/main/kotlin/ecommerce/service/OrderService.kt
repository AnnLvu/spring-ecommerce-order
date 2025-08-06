package ecommerce.service

import ecommerce.dto.order.PlaceOrderRequest
import ecommerce.dto.order.PlaceOrderResponse
import ecommerce.dto.stripe.PaymentRequest
import ecommerce.exception.PaymentException
import ecommerce.model.Order
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
    private val cartService: CartService,
    private val orderRepository: OrderRepository,
    private val stripeClient: StripeClient
) {
    @Transactional
    fun placeOrder(userId: Long, request: PlaceOrderRequest): PlaceOrderResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Invalid user ID") }

        val option = optionRepository.findById(request.productOptionId)
            .orElseThrow { IllegalArgumentException("Invalid option ID") }

        if (option.quantity < request.quantity) throw IllegalArgumentException("Insufficient stock")

        val amount = option.price * request.quantity

        val paymentResponse = try {
            stripeClient.createCheckoutSession(
                PaymentRequest(amount, request.currency, request.paymentMethod)
            )
        } catch (e: Exception) {
            throw PaymentException(e.message ?: "Payment failed")
        }
        val sessionId = paymentResponse.id

        option.quantity -= request.quantity
        optionRepository.save(option)

        cartService.removeProductFromCart(user, request.productOptionId)

        val order = Order(
            user,
            option,
            request.quantity,
            sessionId.toString(),
            amount
        )
        val saved = orderRepository.save(order)
        return PlaceOrderResponse(
            saved.id,
            sessionId.toString()
        )

    }

}