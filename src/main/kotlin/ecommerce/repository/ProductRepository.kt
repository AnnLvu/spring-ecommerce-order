package ecommerce.repository

import ecommerce.jpaEntity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByName(name: String): Optional<Product>

    fun existsByName(name: String): Boolean
}
