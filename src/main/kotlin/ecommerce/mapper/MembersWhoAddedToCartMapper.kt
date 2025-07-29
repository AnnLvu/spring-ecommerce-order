package ecommerce.mapper

import ecommerce.entity.MembersWhoAddedToCart
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class MembersWhoAddedToCartMapper : RowMapper<MembersWhoAddedToCart> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): MembersWhoAddedToCart {
        return MembersWhoAddedToCart(
            name = rs.getString("name"),
            id = rs.getLong("id"),
            email = rs.getString("email"),
        )
    }
}
