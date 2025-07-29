package ecommerce.dto.user

import ecommerce.enums.UserRole

class MemberUser(
    val id: Long? = null,
    val email: String,
    val name: String,
    val role: UserRole = UserRole.USER,
)
