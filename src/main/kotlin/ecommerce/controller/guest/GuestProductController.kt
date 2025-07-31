package ecommerce.controller.guest

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

    companion object {
        private const val PER_PAGE = 10
        private const val DEFAULT_PAGE = 1
    }
}
