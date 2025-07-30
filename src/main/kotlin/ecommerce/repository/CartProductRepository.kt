package ecommerce.repository

import ecommerce.entity.CartProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartProductRepository : JpaRepository<CartProduct, Long>
