package ecommerce.mapper

import ecommerce.entity.TopAddedProducts
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class TopAddedProductsMapper : RowMapper<TopAddedProducts> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): TopAddedProducts {
        return TopAddedProducts(
            name = rs.getString("name"),
            count = rs.getInt("count"),
            createdAt = rs.getString("created_at"),
        )
    }
}
