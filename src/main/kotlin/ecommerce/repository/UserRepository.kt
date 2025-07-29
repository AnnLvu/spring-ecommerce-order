package ecommerce.repository

import ecommerce.entity.User
import ecommerce.mapper.UserRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val userRowMapper: UserRowMapper,
) {
    fun create(user: User): Long {
        val insert =
            SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id")

        val parameters =
            mapOf(
                "email" to user.email,
                "password" to user.password,
                "name" to user.name,
                "role" to user.role,
            )
        return insert.executeAndReturnKey(parameters).toLong()
    }

    fun findByEmailAndPassword(
        email: String,
        password: String,
    ): User? {
        val sql = "select * from users where email = ? and password = ?"
        val res = jdbcTemplate.query(sql, userRowMapper, email, password)
        return res.firstOrNull()
    }

    fun findByEmail(email: String): User? {
        val sql = "select * from users where email = ?"
        val res = jdbcTemplate.query(sql, userRowMapper, email)
        return res.firstOrNull()
    }

    fun existsByEmail(email: String): Boolean {
        val sql = "select exists(select 1 from users where email = ?)"
        return jdbcTemplate.queryForObject(sql, Int::class.java, email)
            ?.let { it > 0 } ?: false
    }
}
