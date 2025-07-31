package ecommerce.service

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
                price = 15.0,
                quantity = 10,
                imageUrl = "test.png",
            ),
        )
        val productListResponse = adminProductService.getAllProducts()
        assertThat(productListResponse.products.size).isEqualTo(1)
    }

    @Test
    fun getProductById() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
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
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
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
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        assertThrows<DuplicateProductNameException> {
            adminProductService.createProduct(
                ProductDTO(
                    name = product.name,
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
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
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        adminProductService.updateProduct(product.id, ProductDTO(name = "test", price = 11.0, imageUrl = "test.png", quantity = 11))
        assertThat(productRepository.findById(product.id).orElse(null).price).isEqualTo(11.0)
    }

    @Test
    fun `throws error if no product found updateProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.updateProduct(-3, ProductDTO(name = "test", price = 11.0, imageUrl = "test.png", quantity = 11))
        }
    }

    @Test
    fun `throws error if duplicate name updateProduct`() {
        productRepository.save(
            Product(
                name = "test-1",
                price = 15.0,
                quantity = 10,
                imageUrl = "test.png",
            ),
        )
        val product2 =
            productRepository.save(
                Product(
                    name = "test-2",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        assertThrows<DuplicateProductNameException> {
            adminProductService.updateProduct(product2.id, ProductDTO(name = "test-1", price = 11.0, imageUrl = "test.png", quantity = 11))
        }
    }

    @Test
    fun patchProduct() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        adminProductService.patchProduct(product.id, ProductPatchDTO(price = 21.0))
        assertThat(productRepository.findById(product.id).orElse(null).price).isEqualTo(21.0)
    }

    @Test
    fun `throws error if no product found patchProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.patchProduct(-3, ProductPatchDTO(name = "test", price = 11.0, imageUrl = "test.png", quantity = 11))
        }
    }

    @Test
    fun `throws error if duplicate name patchProduct`() {
        productRepository.save(
            Product(
                name = "test-1",
                price = 15.0,
                quantity = 10,
                imageUrl = "test.png",
            ),
        )
        val product2 =
            productRepository.save(
                Product(
                    name = "test-2",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        assertThrows<DuplicateProductNameException> {
            adminProductService.patchProduct(
                product2.id,
                ProductPatchDTO(name = "test-1", price = 11.0, imageUrl = "test.png", quantity = 11),
            )
        }
    }

    @Test
    fun `updates all fields with same name on patch`() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
                ),
            )
        adminProductService.patchProduct(product.id, ProductPatchDTO(name = "test", price = 11.0, imageUrl = "tests.png", quantity = 11))
        assertThat(productRepository.findById(product.id).orElse(null).price).isEqualTo(11.0)
    }

    @Test
    fun deleteProduct() {
        val product =
            productRepository.save(
                Product(
                    name = "test",
                    price = 15.0,
                    quantity = 10,
                    imageUrl = "test.png",
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
