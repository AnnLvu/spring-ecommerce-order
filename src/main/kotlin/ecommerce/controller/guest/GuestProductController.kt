package ecommerce.controller.guest

import ecommerce.dto.products.ProductDTO
import ecommerce.service.GuestProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class GuestProductController(
    private val guestProductService: GuestProductService,
) {
    @GetMapping("/products")
    fun listProducts(): ResponseEntity<List<ProductDTO>> {
        val products = guestProductService.getListProducts()
        return ResponseEntity.ok().body(products)
    }
}
