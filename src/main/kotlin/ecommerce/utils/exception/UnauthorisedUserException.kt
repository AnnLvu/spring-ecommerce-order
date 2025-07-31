package ecommerce.utils.exception

class UnauthorisedUserException(message: String? = null) : RuntimeException(message ?: "Please log in first")
