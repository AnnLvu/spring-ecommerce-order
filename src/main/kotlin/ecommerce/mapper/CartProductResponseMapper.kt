package ecommerce.mapper

import ecommerce.entity.CartProductResponse
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class CartProductResponseMapper : RowMapper<CartProductResponse> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): CartProductResponse {
        return CartProductResponse(
            productId = rs.getLong("product_id"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            price = rs.getDouble("price"),
            imageUrl = rs.getString("image_url"),
            quantity = rs.getInt("quantity"),
        )
    }
}
