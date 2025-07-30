package ecommerce.controller.member

import ecommerce.annotations.LoginMember
import ecommerce.dto.cartProduct.CartProductResponseDTO
import ecommerce.dto.response.MessageResponse
import ecommerce.jpaEntity.User
import ecommerce.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/member/cart")
class CartController(
    private val cartService: CartService,
) {
    @GetMapping("")
    fun getCartItems(
        @LoginMember user: User,
    ): ResponseEntity<List<CartProductResponseDTO>> {
        val products = cartService.getCartProducts(user)
        return ResponseEntity.ok(products)
    }

    @PostMapping("/{id}")
    fun addProduct(
        @LoginMember user: User,
        @PathVariable("id") productID: Long,
    ): ResponseEntity<MessageResponse> {
        val id = cartService.addProductToCart(user, productID)
        return ResponseEntity.created(URI.create("/cart/$id")).body(MessageResponse("Product added to cart"))
    }

    @DeleteMapping("/{id}")
    fun removeProduct(
        @LoginMember user: User,
        @PathVariable("id") productID: Long,
    ): ResponseEntity<String> {
        cartService.removeProductFromCart(user, productID)
        return ResponseEntity.noContent().build()
    }
}
