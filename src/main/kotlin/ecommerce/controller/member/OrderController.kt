package ecommerce.controller.member

import ecommerce.annotations.LoginMember
import ecommerce.dto.order.PlaceOrderRequest
import ecommerce.dto.order.PlaceOrderResponse
import ecommerce.model.User
import ecommerce.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping
    fun placeOrder(
        @LoginMember user: User,
        @RequestBody request: PlaceOrderRequest
    ): ResponseEntity<PlaceOrderResponse> {
        val resp = orderService.placeOrder(user.id, request)
        return ResponseEntity.ok(resp)
    }
}