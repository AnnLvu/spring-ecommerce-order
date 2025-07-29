package ecommerce.repository

import ecommerce.entity.MembersWhoAddedToCart
import ecommerce.entity.TopAddedProducts
import ecommerce.enums.CartAction
import ecommerce.mapper.MembersWhoAddedToCartMapper
import ecommerce.mapper.TopAddedProductsMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

@Repository
class CartStatisticsRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val topAddedProductsMapper: TopAddedProductsMapper,
    private val membersWhoAddedToCartMapper: MembersWhoAddedToCartMapper,
) {
    fun create(
        userID: Long?,
        productID: Long,
        action: CartAction,
    ): Long {
        val insert =
            SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cart_statistics")
                .usingGeneratedKeyColumns("id")

        val parameters =
            mapOf(
                "action" to action,
                "product_id" to productID,
                "user_id" to userID,
            )
        return insert.executeAndReturnKey(parameters).toLong()
    }

    fun getTopAddedProducts(limit: Int = 5): List<TopAddedProducts> {
        val sql =
            """
            select 
                p.name as name,
                count(*) as count,
                max(cs.created_at) as created_at
            from cart_statistics cs
            join products p on cs.product_id = p.id
            where cs.action = ? 
              and cs.created_at >= dateadd('day', -30, current_timestamp)
            group by p.name
            order by count desc, max(cs.created_at) desc
            limit ?
            """.trimIndent()

        return jdbcTemplate.query(sql, topAddedProductsMapper, CartAction.ADD.name, limit)
    }

    fun getMembersWhoAddedToCart(days: Int = 7): List<MembersWhoAddedToCart> {
        val sql =
            """
            select
                u.id as id,
                u.name as name,
                u.email as email
            from cart_statistics c
            join users u on c.user_id = u.id
            where c.created_at >= dateadd('day', -?, current_timestamp)
            group by u.id, u.name, u.email
            order by max(c.created_at) desc
            """.trimIndent()
        return jdbcTemplate.query(sql, membersWhoAddedToCartMapper, days)
    }
}
