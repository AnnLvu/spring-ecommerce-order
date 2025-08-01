package ecommerce.service

import ecommerce.dto.products.OptionDTO
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.model.Option
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.DuplicateProductNameException
import ecommerce.utils.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class ProductServiceTest {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var adminProductService: AdminProductService

    @Autowired
    private lateinit var optionRepository: OptionRepository

    @AfterEach
    fun initAfter() {
        optionRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun getAllProducts() {
        createProduct()
        val paginatedProducts = adminProductService.getAllProducts()
        assertThat(paginatedProducts.content.size).isEqualTo(1)
    }

    @Test
    fun getProductById() {
        val product = createProduct()
        assertThat(adminProductService.getProductById(product.id)).isNotNull
    }

    @Test
    fun `throws error if no product found getProductById`() {
        assertThrows<EntityNotFoundException> { adminProductService.getProductById(-3) }
    }

    @Test
    fun create() {
        val uri =
            adminProductService.createProduct(
                ProductDTO(
                    name = "test",
                    optionsList =
                        mutableListOf(
                            OptionDTO("Option 1", 10.0, 5, "https://example.com/test.png"),
                        ),
                ),
            )
        assertThat(uri).isNotNull
    }

    @Test
    fun `throws error if duplicate name createProduct`() {
        val product = createProduct()
        assertThrows<DuplicateProductNameException> {
            adminProductService.createProduct(
                ProductDTO(
                    name = product.name,
                    optionsList =
                        mutableListOf(
                            OptionDTO("Option 1", 15.0, 10, "https://example.com/test.png"),
                        ),
                ),
            )
        }
    }

    @Test
    fun updateProduct() {
        val product = createProduct()
        adminProductService.updateProduct(
            product.id,
            ProductDTO(
                name = "test",
                optionsList =
                    mutableListOf(
                        OptionDTO("Option 1", 11.0, 11, "https://example.com/test.png"),
                    ),
            ),
        )
        assertThat(productRepository.findById(product.id).orElse(null).options.first().price).isEqualTo(11.0)
    }

    @Test
    fun `throws error if no product found updateProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.updateProduct(
                -3,
                ProductDTO(
                    name = "test",
                    optionsList =
                        mutableListOf(
                            OptionDTO("Option 1", 11.0, 11, "https://example.com/test.png"),
                        ),
                ),
            )
        }
    }

    @Test
    fun `throws error if duplicate name updateProduct`() {
        val product1 = createProduct()
        val product2 = createProduct("Tester")
        assertThrows<DuplicateProductNameException> {
            adminProductService.updateProduct(
                product2.id,
                ProductDTO(
                    product1.name,
                    mutableListOf(
                        OptionDTO("Option 1", 11.0, 11, "https://example.com/test.png"),
                    ),
                ),
            )
        }
    }

    @Test
    fun patchProduct() {
        val product = createProduct()
        adminProductService.patchProduct(product.id, ProductPatchDTO("updated"))
        val updatedProduct = productRepository.findById(product.id).orElse(null)
        assertThat(updatedProduct.name).isEqualTo("updated")
    }

    @Test
    fun `throws error if no product found patchProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.patchProduct(
                -3,
                ProductPatchDTO("test"),
            )
        }
    }

    @Test
    fun `throws error if duplicate name patchProduct`() {
        val product1 = createProduct()
        val product2 = createProduct("Tester")
        assertThrows<DuplicateProductNameException> {
            adminProductService.patchProduct(
                product2.id,
                ProductPatchDTO(product1.name),
            )
        }
    }

    @Test
    fun `updates all fields with same name on patch`() {
        val product = createProduct()
        assertDoesNotThrow {
            adminProductService.patchProduct(
                product.id,
                ProductPatchDTO(product.name),
            )
        }
    }

    @Test
    fun deleteProduct() {
        val product = createProduct()
        adminProductService.deleteProduct(product.id)
        assertThat(productRepository.findById(product.id).orElse(null)).isNull()
    }

    @Test
    fun `Throws error if product not found deleteProduct`() {
        assertThrows<EntityNotFoundException> { adminProductService.deleteProduct(-3) }
    }

    private fun createProduct(name: String = "name"): Product {
        return productRepository.save(
            Product(
                name,
                mutableListOf(
                    Option(
                        "name",
                        10.1,
                        51,
                        "http://localhost:8080/image/upload/product1.jpg",
                    ),
                ),
            ),
        )
    }
}
