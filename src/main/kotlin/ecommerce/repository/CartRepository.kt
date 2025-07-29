package ecommerce.repository

import ecommerce.entity.Cart
import ecommerce.mapper.CartRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CartRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val cartRowMapper: CartRowMapper,
) {
    fun createCartForUser(userId: Long) {
        val sql = "insert into cart (user_id) values ?"
        jdbcTemplate.update(sql, userId)
    }

    fun findMembersCart(userId: Long?): Cart? {
        val sql = "select * from cart where user_id = ?"
        val res = jdbcTemplate.query(sql, cartRowMapper, userId)
        return res.firstOrNull()
    }
}
