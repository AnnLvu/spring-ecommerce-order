package ecommerce.entity

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1,
)
