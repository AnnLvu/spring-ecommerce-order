package ecommerce.exception

class UnauthorisedUserException(message: String? = null) : RuntimeException(message ?: "Please log in first")
