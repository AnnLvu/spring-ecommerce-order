package ecommerce.repository

import ecommerce.model.Cart
import ecommerce.model.CartProduct
import ecommerce.model.Option
import ecommerce.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartProductRepository : JpaRepository<CartProduct, Long> {
    fun findByCart(cart: Cart): List<CartProduct>

    fun deleteByCartAndOption(cart: Cart, option: Option)

}
