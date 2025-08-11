package ecommerce.controller.member

import ecommerce.dto.order.PlaceOrderRequest
import ecommerce.dto.order.PlaceOrderResponse
import ecommerce.enums.UserRole
import ecommerce.model.Cart
import ecommerce.model.User
import ecommerce.service.OrderService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class OrderControllerTest {
    private lateinit var orderService: OrderService
    private lateinit var controller: OrderController

    @BeforeEach
    fun setUp() {
        orderService = mockk()
        controller = OrderController(orderService)
    }

    @Test
    fun `placeOrder should return 200 and body`() {
        val cart = Cart(id = 10L)

        val user =
            User(
                "email",
                "pass",
                "name",
                UserRole.USER,
                cart,
                7L,
            )

        val request =
            PlaceOrderRequest(
                currency = "usd",
                paymentMethodId = "pm_card_visa",
            )
        val expected = PlaceOrderResponse(55L, "sess_11")

        every { orderService.placeOrder(user.id, request) } returns expected

        // when
        val responseEntity = controller.placeOrder(user, request)

        // then
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(expected, responseEntity.body)
    }
}
