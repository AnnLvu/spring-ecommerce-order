package ecommerce.controller.guest

import ecommerce.controller.admin.AdminProductController.Companion.DEFAULT_PAGE
import ecommerce.controller.admin.AdminProductController.Companion.PER_PAGE
import ecommerce.dto.products.ProductListResponse
import ecommerce.service.GuestProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class GuestProductController(
    private val guestProductService: GuestProductService,
) {
    @GetMapping("/products")
    fun listProducts(
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE.toString()) page: Int,
        @RequestParam(value = "perPage", defaultValue = PER_PAGE.toString()) perPage: Int,
    ): ResponseEntity<ProductListResponse> {
        val productListResponse = guestProductService.getListProducts(page, perPage)
        return ResponseEntity.ok().body(productListResponse)
    }
}
