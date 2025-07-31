package ecommerce.service

import ecommerce.dto.wishList.WishListResponse
import ecommerce.model.Product
import ecommerce.model.User
import ecommerce.model.WishListProduct
import ecommerce.repository.ProductRepository
import ecommerce.repository.WishListRepository
import ecommerce.utils.exception.DuplicateProductNameException
import ecommerce.utils.exception.EntityNotFoundException
import ecommerce.utils.extensions.toDTO
import org.springframework.stereotype.Service
import java.net.URI

@Service
class WishListService(
    private val wishRepository: WishListRepository,
    private val productRepository: ProductRepository,
    private val cartService: CartService,
) {
    fun getProducts(member: User): WishListResponse {
        val wishListProducts = wishRepository.findAllByUserId(member.id)
        return WishListResponse(
            wishListProducts.map { it.toDTO() },
        )
    }

    fun addProduct(
        member: User,
        productId: Long,
    ): URI {
        val wishList = getWishList(member)
        val wishListProduct = wishList.find { it.id == productId }

        if (wishListProduct != null) {
            throw DuplicateProductNameException()
        }

        val product = getValidProduct(productId)
        val newItem = wishRepository.save(WishListProduct(member, product))
        wishList.add(newItem)
        return URI.create("/member/wish/${newItem.id}")
    }

    fun removeProduct(
        member: User,
        productId: Long,
    ) {
        val wishList = getWishList(member)
        val wishListProduct = wishList.find { it.id == productId }
        if (wishListProduct == null) throw EntityNotFoundException("Product not found")

        wishList.remove(wishListProduct)
    }

    fun moveToCart(
        member: User,
        productId: Long,
    ) {
        val wishList = getWishList(member)
        val wishListProduct = wishList.find { it.id == productId }
        if (wishListProduct == null) throw EntityNotFoundException("Product not found")

        cartService.addProductToCart(member, productId)
        wishList.remove(wishListProduct)
    }

    private fun getWishList(member: User): MutableList<WishListProduct> {
        return member.wishListItems ?: throw EntityNotFoundException("Wish list not found")
    }

    private fun getValidProduct(productID: Long): Product {
        return productRepository.findById(productID).orElseThrow {
            EntityNotFoundException("Product not found")
        }
    }
}
