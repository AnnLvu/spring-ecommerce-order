package ecommerce.utils.exception

class UserCredentialException(message: String? = null) : RuntimeException(message ?: "Not valid Credential")
