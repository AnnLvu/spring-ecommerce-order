package ecommerce.repository

import ecommerce.model.WishListProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface WishListRepository : JpaRepository<WishListProduct, Long> {
    fun findAllByUserId(userId: Long): List<WishListProduct>

    fun findByUserIdAndProductId(
        userId: Long,
        productId: Long,
    ): Optional<WishListProduct>
}
