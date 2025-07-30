package ecommerce.repository

import ecommerce.dto.cartStatistics.MembersWhoAddedToCartDTO
import ecommerce.dto.cartStatistics.TopAddedProductsDTO
import ecommerce.entity.CartStatistics
import ecommerce.enums.CartAction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartStatisticsRepository : JpaRepository<CartStatistics, Long> {
    @Query(
        """
    SELECT new ecommerce.dto.cartStatistics.TopAddedProductsDTO(
        cs.product.name,
        COUNT(cs),
        MAX(cs.createdAt)
    )
    FROM CartStatistics cs
    WHERE cs.action = :action
      AND cs.createdAt >= :since
    GROUP BY cs.product.name
    ORDER BY COUNT(cs) DESC, MAX(cs.createdAt) DESC
    """,
    )
    fun findTopProducts(
        @Param("action") action: CartAction,
        @Param("since") since: LocalDateTime,
        pageable: org.springframework.data.domain.Pageable,
    ): List<TopAddedProductsDTO>

    @Query(
        """
    SELECT new ecommerce.dto.cartStatistics.MembersWhoAddedToCartDTO(
        cs.user.id,
        cs.user.name,
        cs.user.email
    )
    FROM CartStatistics cs
    WHERE cs.createdAt >= :since
    GROUP BY cs.user.id, cs.user.name, cs.user.email
    ORDER BY MAX(cs.createdAt) DESC
    """,
    )
    fun findActiveUsersSince(
        @Param("since") since: LocalDateTime,
    ): List<MembersWhoAddedToCartDTO>
}
