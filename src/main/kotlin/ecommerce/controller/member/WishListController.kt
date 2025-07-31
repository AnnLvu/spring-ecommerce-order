package ecommerce.controller.member

import ecommerce.controller.admin.AdminProductController.Companion.DEFAULT_PAGE
import ecommerce.controller.admin.AdminProductController.Companion.PER_PAGE
import ecommerce.model.User
import ecommerce.model.WishListProduct
import ecommerce.service.WishListService
import ecommerce.utils.annotations.LoginMember
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member/wish-list")
class WishListController(private val wishListService: WishListService) {
    @GetMapping("")
    fun getWishList(
        @LoginMember member: User,
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE.toString()) page: Int,
        @RequestParam(value = "perPage", defaultValue = PER_PAGE.toString()) perPage: Int,
    ): ResponseEntity<Page<WishListProduct>> {
        val wishListResponse = wishListService.getProducts(member, page, perPage)
        return ResponseEntity.ok().body(wishListResponse)
    }

    @PostMapping("/{productId}")
    fun addProduct(
        @LoginMember member: User,
        @PathVariable("productId") productID: Long,
    ): ResponseEntity<Void> {
        val uri = wishListService.addProduct(member, productID)
        return ResponseEntity.created(uri).build()
    }

    @PostMapping("/move-to-cart/{productId}")
    fun moveProductToCart(
        @LoginMember member: User,
        @PathVariable("productId") productID: Long,
    ): ResponseEntity<Void> {
        wishListService.removeProduct(member, productID)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{productId}")
    fun removeProduct(
        @LoginMember member: User,
        @PathVariable("productId") productID: Long,
    ): ResponseEntity<Void> {
        wishListService.removeProduct(member, productID)
        return ResponseEntity.noContent().build()
    }
}
