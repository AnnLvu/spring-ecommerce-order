package ecommerce.mapper

import ecommerce.entity.CartProduct
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class CartProductMapper : RowMapper<CartProduct> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): CartProduct {
        return CartProduct(
            id = rs.getLong("id"),
            cartId = rs.getLong("cart_id"),
            productID = rs.getLong("product_id"),
            quantity = rs.getInt("quantity"),
        )
    }
}
