package ecommerce.service

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.exception.DuplicateProductNameException
import ecommerce.exception.EntityNotFoundException
import ecommerce.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun getAllProducts() {
        val products = adminProductService.getAllProducts()
        assertThat(products).isNotEmpty
    }

    @Test
    fun getProductById() {
        val id = createProduct("getProductById")
        val product = adminProductService.getProductById(id)
        assertThat(product.id).isEqualTo(id)
    }

    @Test
    fun `throws error if no product found getProductById`() {
        assertThrows<EntityNotFoundException> { adminProductService.getProductById(-3) }
    }

    @Test
    fun createProduct() {
        val uri =
            adminProductService.createProduct(
                ProductDTO(name = "Product1", description = "description", price = 10.5, imageUrl = "url.com"),
            )
        assertThat(uri).isNotNull
    }

    @Test
    fun `throws error if duplicate name createProduct`() {
        createProduct("createProduct")
        assertThrows<DuplicateProductNameException> {
            adminProductService.createProduct(
                ProductDTO(name = "createProduct", description = "description", price = 10.5, imageUrl = "url.com"),
            )
        }
    }

    @Test
    fun updateProduct() {
        val id = createProduct("updateProduct")
        adminProductService.updateProduct(
            id = id,
            product = ProductDTO(name = "updateProduct2", description = "description", price = 10.5, imageUrl = "url.com"),
        )
        val product = adminProductService.getProductById(id)
        assertThat(product.name).isEqualTo("updateProduct2")
    }

    @Test
    fun `throws error if no product found updateProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.updateProduct(
                -1,
                ProductDTO(name = "Product1", description = "description", price = 10.5, imageUrl = "url.com"),
            )
        }
    }

    @Test
    fun `throws error if duplicate name updateProduct`() {
        createProduct("uniqueName")

        val id = createProduct("someName")
        val product = adminProductService.getProductById(id)
        val newProduct =
            product.copy(
                name = "uniqueName",
            )

        assertThrows<DuplicateProductNameException> {
            adminProductService.updateProduct(id, newProduct)
        }
    }

    @Test
    fun patchProduct() {
        val id = createProduct("patchProduct")
        adminProductService.patchProduct(id, ProductPatchDTO(name = "Patched"))
        val product = adminProductService.getProductById(id)
        assertThat(product.name).isEqualTo("Patched")
    }

    @Test
    fun `throws error if no product found patchProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.patchProduct(
                -1,
                ProductPatchDTO(name = "Something"),
            )
        }
    }

    @Test
    fun `throws error if duplicate name patchProduct`() {
        createProduct("uniqueName2")

        val id = createProduct("patchName")
        val patch = ProductPatchDTO(name = "uniqueName2")

        assertThrows<DuplicateProductNameException> {
            adminProductService.patchProduct(id, patch)
        }
    }

    @Test
    fun `updates all fields on patch`() {
        val id = createProduct("Name1122")
        val patch =
            ProductPatchDTO(
                name = "Name1133",
                description = "description",
                price = 10.5,
                imageUrl = "url.com",
                quantity = 33,
            )
        adminProductService.patchProduct(id, patch)
        assertThat(adminProductService.getProductById(id).description).isEqualTo("description")
    }

    @Test
    fun `updates all fields with same name on patch`() {
        val id = createProduct("Name1122")
        val patch =
            ProductPatchDTO(
                name = "Name1122",
                description = "description",
                price = 10.5,
                imageUrl = "url.com",
                quantity = 33,
            )
        adminProductService.patchProduct(id, patch)
        assertThat(adminProductService.getProductById(id).description).isEqualTo("description")
    }

    @Test
    fun deleteProduct() {
        val id = createProduct("deleteProduct")
        adminProductService.deleteProduct(id)
        assertThrows<EntityNotFoundException> { adminProductService.getProductById(id) }
    }

    @Test
    fun `Throws error if product not found deleteProduct`() {
        assertThrows<EntityNotFoundException> { adminProductService.deleteProduct(-3) }
    }

    private fun createProduct(name: String): Long {
        return productRepository.create(
            ProductDTO(name = name, description = "description", price = 10.5, imageUrl = "url.com"),
        )
    }
}
