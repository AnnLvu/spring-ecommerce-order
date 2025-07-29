package ecommerce.controller.admin

import ecommerce.dto.products.ProductDTO
import ecommerce.dto.products.ProductPatchDTO
import ecommerce.dto.response.MessageResponse
import ecommerce.service.AdminProductService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/products")
class AdminProductController(private val adminProductService: AdminProductService) {
    @GetMapping("")
    fun getProducts(): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity.ok().body(adminProductService.getAllProducts())
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable("id") id: Long,
    ): ResponseEntity<ProductDTO> {
        return ResponseEntity.ok().body(adminProductService.getProductById(id))
    }

    @PostMapping("")
    fun create(
        @RequestBody @Valid product: ProductDTO,
    ): ResponseEntity<MessageResponse> {
        val uri = adminProductService.createProduct(product)
        return ResponseEntity.created(uri).body(MessageResponse("Product created"))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid newProduct: ProductDTO,
    ): ResponseEntity<MessageResponse> {
        adminProductService.updateProduct(id, newProduct)
        return ResponseEntity.ok().body(MessageResponse("Product updated"))
    }

    @PatchMapping("/{id}")
    fun edit(
        @PathVariable("id") id: Long,
        @RequestBody @Valid patchProduct: ProductPatchDTO,
    ): ResponseEntity<MessageResponse> {
        adminProductService.patchProduct(id, patchProduct)
        return ResponseEntity.ok().body(MessageResponse("Product updated"))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") id: Long,
    ): ResponseEntity<String> {
        adminProductService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
