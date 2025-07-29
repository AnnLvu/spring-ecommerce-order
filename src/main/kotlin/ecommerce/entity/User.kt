package ecommerce.entity

import ecommerce.enums.UserRole

class User(
    val id: Long? = null,
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
)
