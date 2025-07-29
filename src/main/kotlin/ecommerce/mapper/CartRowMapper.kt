package ecommerce.mapper

import ecommerce.entity.Cart
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class CartRowMapper : RowMapper<Cart> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): Cart {
        return Cart(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
        )
    }
}
