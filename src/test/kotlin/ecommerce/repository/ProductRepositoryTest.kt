package ecommerce.repository

import ecommerce.dto.products.ProductDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProductRepositoryTest {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun findAll() {
        val products = productRepository.findAll()
        assertThat(products.size).isNotZero
    }

    @Test
    fun findById() {
        val id = createProduct("findById")
        val product = productRepository.findById(id)
        assertThat(product?.name).isEqualTo("findById")
    }

    @Test
    fun create() {
        val id = createProduct("create")
        assertThat(productRepository.findById(id)).isNotNull()
    }

    @Test
    fun update() {
        val id = createProduct("update")
        productRepository.update(
            id,
            ProductDTO(name = "Product 2", price = 10.5, imageUrl = "url.com", description = "description"),
        )
        val product = productRepository.findById(id)
        assertThat(product?.name).isEqualTo("Product 2")
    }

    @Test
    fun deleteById() {
        val id = createProduct("delete")
        productRepository.deleteById(id)
        val product = productRepository.findById(id)
        assertThat(product).isNull()
    }

    private fun createProduct(name: String): Long {
        return productRepository.create(
            ProductDTO(name = name, description = "description", price = 10.5, imageUrl = "url.com"),
        )
    }
}
