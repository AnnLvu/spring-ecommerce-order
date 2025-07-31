package ecommerce.dto.products

import org.springframework.data.domain.Page

class ProductListResponse(
    val products: Page<ProductResponseDTO>,
)
