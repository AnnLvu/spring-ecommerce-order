package ecommerce.repository

import ecommerce.dto.cartStatistics.MembersWhoAddedToCartView
import ecommerce.dto.cartStatistics.TopAddedProductView
import ecommerce.enums.CartAction
import ecommerce.model.CartStatistic
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartStatisticRepository : JpaRepository<CartStatistic, Long> {
    @Query(
        """
        SELECT cs.option.name AS productName,
               COUNT(cs) AS count,
               MAX(cs.createdAt) AS createdAt
        FROM CartStatistic cs
        WHERE cs.action = :action
          AND cs.createdAt >= :since
        GROUP BY cs.option.name
        ORDER BY COUNT(cs) DESC, MAX(cs.createdAt) DESC
    """,
    )
    fun findTopProducts(
        @Param("action") action: CartAction,
        @Param("since") since: LocalDateTime,
        pageable: Pageable,
    ): List<TopAddedProductView>

    @Query(
        """
        SELECT cs.user.id AS id,
               cs.user.name AS name,
               cs.user.email AS email
        FROM CartStatistic cs
        WHERE cs.createdAt >= :since
        GROUP BY cs.user.id, cs.user.name, cs.user.email
        ORDER BY MAX(cs.createdAt) DESC
    """,
    )
    fun findActiveUsersSince(
        @Param("since") since: LocalDateTime,
    ): List<MembersWhoAddedToCartView>
}
