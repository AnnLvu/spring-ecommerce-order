package ecommerce.utils.extensions

import ecommerce.dto.wishList.WishListProductDTO
import ecommerce.model.WishListProduct

fun WishListProduct.toDTO(): WishListProductDTO {
    val product = this.product
    return WishListProductDTO(
        product.id,
        product.name,
        product.imageUrl,
        product.price,
    )
}
