package ecommerce.controller.admin

import ecommerce.dto.products.OptionDto
import ecommerce.dto.products.OptionPatchDto
import ecommerce.dto.products.ProductDto
import ecommerce.dto.products.ProductPatchDto
import ecommerce.dto.products.ProductResponseDto
import ecommerce.dto.response.MessageResponse
import ecommerce.service.AdminProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/products")
class AdminProductController(private val adminProductService: AdminProductService) {
    @GetMapping("")
    fun getProducts(
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE.toString()) page: Int,
        @RequestParam(value = "perPage", defaultValue = PER_PAGE.toString()) perPage: Int,
    ): ResponseEntity<Page<ProductResponseDto>> {
        val productListResponse = adminProductService.getAllProducts(page, perPage)
        return ResponseEntity.ok().body(productListResponse)
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable("id") id: Long,
    ): ResponseEntity<ProductResponseDto> {
        return ResponseEntity.ok().body(adminProductService.getProductById(id))
    }

    @PostMapping("")
    fun create(
        @RequestBody @Valid product: ProductDto,
    ): ResponseEntity<MessageResponse> {
        val uri = adminProductService.createProduct(product)
        return ResponseEntity.created(uri).body(MessageResponse("Product created"))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid newProduct: ProductDto,
    ): ResponseEntity<MessageResponse> {
        adminProductService.updateProduct(id, newProduct)
        return ResponseEntity.ok().body(MessageResponse("Product updated"))
    }

    @PatchMapping("/{id}")
    fun edit(
        @PathVariable("id") id: Long,
        @RequestBody @Valid patchProduct: ProductPatchDto,
    ): ResponseEntity<MessageResponse> {
        adminProductService.patchProduct(id, patchProduct)
        return ResponseEntity.ok().body(MessageResponse("Product updated"))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") id: Long,
    ): ResponseEntity<Void> {
        adminProductService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/options")
    fun options(
        @PathVariable("id") productId: Long,
    ): ResponseEntity<ProductResponseDto> {
        val options = adminProductService.getProductOptions(productId)
        return ResponseEntity.ok(options)
    }

    @PostMapping("/{id}/options")
    fun createOption(
        @PathVariable("id") productId: Long,
        @RequestBody @Valid optionDto: OptionDto,
    ): ResponseEntity<MessageResponse> {
        val uri = adminProductService.createOption(productId, optionDto)
        return ResponseEntity.created(uri).body(MessageResponse("Option created"))
    }

    @PutMapping("/{productId}/options/{optionId}")
    fun updateOption(
        @PathVariable("productId") productId: Long,
        @PathVariable("optionId") optionId: Long,
        @RequestBody @Valid optionDto: OptionDto,
    ): ResponseEntity<MessageResponse> {
        adminProductService.updateOption(productId, optionId, optionDto)
        return ResponseEntity.ok(MessageResponse("Option updated"))
    }

    @PatchMapping("/{productId}/options/{optionId}")
    fun patchOption(
        @PathVariable("productId") productId: Long,
        @PathVariable("optionId") optionId: Long,
        @RequestBody @Valid patchDto: OptionPatchDto,
    ): ResponseEntity<MessageResponse> {
        adminProductService.patchOption(productId, optionId, patchDto)
        return ResponseEntity.ok(MessageResponse("Option updated"))
    }

    @DeleteMapping("/{productId}/options/{optionId}")
    fun deleteOption(
        @PathVariable("productId") productId: Long,
        @PathVariable("optionId") optionId: Long,
    ): ResponseEntity<Void> {
        adminProductService.deleteOption(productId, optionId)
        return ResponseEntity.noContent().build()
    }

    companion object {
        const val PER_PAGE = 10
        const val DEFAULT_PAGE = 1
    }
}
