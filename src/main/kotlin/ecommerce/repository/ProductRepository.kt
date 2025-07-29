package ecommerce.repository

import ecommerce.dto.products.ProductDTO
import ecommerce.entity.Product
import ecommerce.mapper.ProductRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val productRowMapper: ProductRowMapper,
) {
    fun findAll(): List<Product> {
        val sql = "select * from products"
        return jdbcTemplate.query(sql, productRowMapper)
    }

    fun findById(id: Long): Product? {
        val sql = "select * from products where id = ?"
        val res = jdbcTemplate.query(sql, productRowMapper, id)
        return res.firstOrNull()
    }

    fun findByName(name: String): Product? {
        val sql = "select * from products where name = ?"
        val res = jdbcTemplate.query(sql, productRowMapper, name)
        return res.firstOrNull()
    }

    fun existsByName(name: String): Boolean {
        val sql = "select count(*) from products where name = ?"
        return jdbcTemplate.queryForObject(sql, Long::class.java, name)!! > 0
    }

    fun create(product: ProductDTO): Long {
        val insert =
            SimpleJdbcInsert(jdbcTemplate)
                .withTableName("products")
                .usingGeneratedKeyColumns("id")

        val parameters =
            mapOf(
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "image_url" to product.imageUrl,
                "quantity" to product.quantity,
            )
        return insert.executeAndReturnKey(parameters).toLong()
    }

    fun update(
        id: Long,
        product: ProductDTO,
    ): Int {
        val sql = "update products set name = ?, price = ?, image_url = ? where id = ?"
        return jdbcTemplate.update(sql, product.name, product.price, product.imageUrl, id)
    }

    fun deleteById(id: Long): Int {
        val sql = "delete from products where id = ?"
        return jdbcTemplate.update(sql, id)
    }
}
