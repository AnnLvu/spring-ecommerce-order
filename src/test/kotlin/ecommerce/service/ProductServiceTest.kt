package ecommerce.service

import ecommerce.dto.products.OptionDTO
import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.model.Product
import ecommerce.repository.ProductRepository
import ecommerce.utils.exception.DuplicateProductNameException
import ecommerce.utils.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
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

    @AfterEach
    fun initAfter() {
        productRepository.deleteAll()
    }

    @Test
    fun getAllProducts() {
        productRepository.save(
            Product(
                name = "test",
            ),
        )
        val paginatedProducts = adminProductService.getAllProducts()
        assertThat(paginatedProducts.content.size).isEqualTo(1)
    }

    @Test
    fun getProductById() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
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
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
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
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
        adminProductService.updateProduct(
            product.id,
            ProductDTO(
                name = "test",
                optionsList =
                    mutableListOf(
                        OptionDTO("Option 1", 11.0, 11, imageUrl = "https://example.com/test.png"),
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
                            OptionDTO("Option 1", 11.0, 11, imageUrl = "https://example.com/test.png"),
                        ),
                ),
            )
        }
    }

    @Test
    fun `throws error if duplicate name updateProduct`() {
        productRepository.save(
            Product(
                name = "test-1",
            ),
        )
        val product2 =
            productRepository.save(
                Product(
                    name = "test-2",
                ),
            )
        assertThrows<DuplicateProductNameException> {
            adminProductService.updateProduct(
                product2.id,
                ProductDTO(
                    name = "test-1",
                    optionsList =
                        mutableListOf(
                            OptionDTO("Option 1", 11.0, 11, imageUrl = "https://example.com/test.png"),
                        ),
                ),
            )
        }
    }

    @Test
    fun patchProduct() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
        adminProductService.patchProduct(product.id, ProductPatchDTO(imageUrl = "updated.png"))
        val updatedProduct = productRepository.findById(product.id).orElse(null)
    }

    @Test
    fun `throws error if no product found patchProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.patchProduct(
                -3,
                ProductPatchDTO(name = "test", imageUrl = "test.png"),
            )
        }
    }

    @Test
    fun `throws error if duplicate name patchProduct`() {
        productRepository.save(
            Product(
                name = "test-1",
            ),
        )
        val product2 =
            productRepository.save(
                Product(
                    name = "test-2",
                ),
            )
        assertThrows<DuplicateProductNameException> {
            adminProductService.patchProduct(
                product2.id,
                ProductPatchDTO(name = "test-1"),
            )
        }
    }

    @Test
    fun `updates all fields with same name on patch`() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
        adminProductService.patchProduct(
            product.id,
            ProductPatchDTO(name = "test", imageUrl = "tests.png"),
        )
        val updatedProduct = productRepository.findById(product.id).orElse(null)
    }

    @Test
    fun deleteProduct() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                ),
            )
        adminProductService.deleteProduct(product.id)
        assertThat(productRepository.findById(product.id).orElse(null)).isNull()
    }

    @Test
    fun `Throws error if product not found deleteProduct`() {
        assertThrows<EntityNotFoundException> { adminProductService.deleteProduct(-3) }
    }
}
