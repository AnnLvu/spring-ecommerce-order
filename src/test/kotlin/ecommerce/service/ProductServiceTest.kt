package ecommerce.service

import ecommerce.dto.products.OptionRequestDto
import ecommerce.dto.products.OptionPatchDto
import ecommerce.dto.products.ProductRequestDto
import ecommerce.dto.products.ProductPatchDto
import ecommerce.model.Option
import ecommerce.model.Product
import ecommerce.repository.OptionRepository
import ecommerce.repository.ProductRepository
import ecommerce.exception.DuplicateProductNameException
import ecommerce.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class ProductServiceTest {
    @Autowired
    private lateinit var adminOptionService: AdminOptionService

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
                ProductRequestDto(
                    name = "test",
                    optionsList =
                        mutableListOf(
                            OptionRequestDto("Option 1", 10.0, 5, "https://example.com/test.png"),
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
                ProductRequestDto(
                    name = product.name,
                    optionsList =
                        mutableListOf(
                            OptionRequestDto("Option 1", 15.0, 10, "https://example.com/test.png"),
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
            ProductRequestDto(
                name = "test",
                optionsList =
                    mutableListOf(
                        OptionRequestDto("Option 1", 11.0, 11, "https://example.com/test.png"),
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
                ProductRequestDto(
                    name = "test",
                    optionsList =
                        mutableListOf(
                            OptionRequestDto("Option 1", 11.0, 11, "https://example.com/test.png"),
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
                ProductRequestDto(
                    product1.name,
                    mutableListOf(
                        OptionRequestDto("Option 1", 11.0, 11, "https://example.com/test.png"),
                    ),
                ),
            )
        }
    }

    @Test
    fun patchProduct() {
        val product = createProduct()
        adminProductService.patchProduct(
            product.id,
            ProductPatchDto("updated", mutableListOf(OptionRequestDto("Option 1", 10.0, 5, "https://example.com/test.png"))),
        )
        val updatedProduct = productRepository.findById(product.id).orElse(null)
        assertThat(updatedProduct.name).isEqualTo("updated")
    }

    @Test
    fun `throws error if no product found patchProduct`() {
        assertThrows<EntityNotFoundException> {
            adminProductService.patchProduct(
                -3,
                ProductPatchDto("test"),
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
                ProductPatchDto(product1.name),
            )
        }
    }

    @Test
    fun `updates all fields with same name on patch`() {
        val product = createProduct()
        assertDoesNotThrow {
            adminProductService.patchProduct(
                product.id,
                ProductPatchDto(product.name),
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

    @Test
    fun getProductOptions() {
        val product = createProduct()
        val response = adminOptionService.getProductOptions(product.id)
        assertThat(response.options).isNotEmpty
    }

    @Test
    fun createOption() {
        val product = createProduct()
        val uri =
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    "option",
                    10.1,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        assertThat(uri).isNotNull
        assertThat(product.options.size).isEqualTo(2)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 100_000_001])
    fun `throws error if quantity not valid`(quantity: Int) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    "test",
                    10.1,
                    quantity,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [-1.0, 0.0])
    fun `throws error if price not valid`(price: Double) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    "test",
                    price,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "      ",
            "",
        ],
    )
    fun `throws error if name is blank`(name: String) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    name,
                    10.1,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", // 51 characters
            "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", // 60 characters
        ],
    )
    fun `throws error if name is length is above max limit`(name: String) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    name,
                    10.1,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "a",
            "John",
            "A very normal name",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", // 50
        ],
    )
    fun `excepts in range names`(name: String) {
        val product = createProduct()
        assertDoesNotThrow {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    name,
                    10.1,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Anna!",
            "😊",
        ],
    )
    fun `throws invalid name error`(name: String) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    name,
                    10.1,
                    51,
                    "http://localhost:8080/image/upload/product1.jpg",
                ),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "http://www.googleapis.com/oauth2/v1/",
            "",
            "http://localhost:8080/image/upload/product1.docs",
        ],
    )
    fun `throws invalid url error`(imageUrl: String) {
        val product = createProduct()
        assertThrows<IllegalArgumentException> {
            adminOptionService.createOption(
                product.id,
                OptionRequestDto(
                    "test",
                    10.1,
                    51,
                    imageUrl,
                ),
            )
        }
    }

    @Test
    fun updateOption() {
        val product = createProduct()
        adminOptionService.updateOption(
            product.id,
            product.options[0].id,
            OptionRequestDto(
                "test",
                10.2,
                52,
                "http://localhost:8080/image/upload/product1.png",
            ),
        )
        val option = product.options[0]
        assertThat(option.name).isEqualTo("test")
        assertThat(option.imageUrl).isEqualTo("http://localhost:8080/image/upload/product1.png")
        assertThat(option.price).isEqualTo(10.2)
        assertThat(option.quantity).isEqualTo(52)
    }

    @Test
    fun `throws for invalid product updateOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                -1,
                product.options[0].id,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
    }

    @Test
    fun `throws for invalid option id updateOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                product.id,
                -1,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
    }

    @Test
    fun patchOption() {
        val product = createProduct()
        adminOptionService.patchOption(
            product.id,
            product.options[0].id,
            OptionPatchDto(
                "tester",
                10.2,
                52,
                "http://localhost:8080/image/upload/product1.png",
            ),
        )
        val option = product.options[0]
        assertThat(option.name).isEqualTo("tester")
        assertThat(option.imageUrl).isEqualTo("http://localhost:8080/image/upload/product1.png")
        assertThat(option.price).isEqualTo(10.2)
        assertThat(option.quantity).isEqualTo(52)
    }

    @Test
    fun `throws for invalid product patchOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                -1,
                product.options[0].id,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
    }

    @Test
    fun `throws for invalid option id patchOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                product.id,
                -1,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
    }

    @Test
    fun deleteOption() {
        val product = createProduct()
        adminOptionService.deleteOption(
            product.id,
            product.options[0].id,
        )
        assertThat(product.options).isEmpty()
    }

    @Test
    fun `throws for invalid product deleteOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                -1,
                product.options[0].id,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
    }

    @Test
    fun `throws for invalid option id deleteOption`() {
        val product = createProduct()
        assertThrows<EntityNotFoundException> {
            adminOptionService.updateOption(
                product.id,
                -1,
                OptionRequestDto(
                    "test",
                    10.2,
                    52,
                    "http://localhost:8080/image/upload/product1.png",
                ),
            )
        }
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
