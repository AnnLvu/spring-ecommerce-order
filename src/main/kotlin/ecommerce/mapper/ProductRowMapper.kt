package ecommerce.mapper

import ecommerce.entity.Product
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class ProductRowMapper : RowMapper<Product> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): Product {
        return Product(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            price = rs.getDouble("price"),
            description = rs.getString("description"),
            imageUrl = rs.getString("image_url"),
        )
    }
}
