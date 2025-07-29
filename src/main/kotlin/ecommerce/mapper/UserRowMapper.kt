package ecommerce.mapper

import ecommerce.entity.User
import ecommerce.enums.UserRole
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class UserRowMapper : RowMapper<User> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): User {
        return User(
            id = rs.getLong("id"),
            email = rs.getString("email"),
            name = rs.getString("name"),
            role = UserRole.valueOf(rs.getString("role")),
            password = rs.getString("password"),
        )
    }
}
