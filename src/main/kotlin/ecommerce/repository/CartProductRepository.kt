package ecommerce.repository

import ecommerce.jpaEntity.CartProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartProductRepository : JpaRepository<CartProduct, Long>
