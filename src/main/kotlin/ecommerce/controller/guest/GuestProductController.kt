package ecommerce.controller.guest

import ecommerce.config.PaginationConstants.DEFAULT_PAGE
import ecommerce.config.PaginationConstants.PER_PAGE
import ecommerce.dto.products.ProductResponseDto
import ecommerce.service.GuestProductService
import org.springframework.data.domain.Page
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
    ): ResponseEntity<Page<ProductResponseDto>> {
        val productListResponse = guestProductService.getListProducts(page, perPage)
        return ResponseEntity.ok().body(productListResponse)
    }
}
