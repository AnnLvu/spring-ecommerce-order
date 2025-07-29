package ecommerce.controller.admin

import ecommerce.dto.cartStatistics.MembersWhoAddedToCartDTO
import ecommerce.dto.cartStatistics.TopAddedProductsDTO
import ecommerce.service.AdminStatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/cart-statistics")
class AdminCartStatisticsController(
    private val adminStatisticsService: AdminStatisticsService,
) {
    @GetMapping("/top-products")
    fun getTopAddedProducts(): ResponseEntity<List<TopAddedProductsDTO>> {
        val res = adminStatisticsService.getTopAddedProducts()
        return ResponseEntity.ok().body(res)
    }

    @GetMapping("/members-added-cart")
    fun getMembersWhoAddedToCart(): ResponseEntity<List<MembersWhoAddedToCartDTO>> {
        val res = adminStatisticsService.getMembersWhoAddedToCart()
        return ResponseEntity.ok().body(res)
    }
}
