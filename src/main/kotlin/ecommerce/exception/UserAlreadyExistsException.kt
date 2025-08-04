package ecommerce.exception

import java.lang.RuntimeException

class UserAlreadyExistsException(email: String) : RuntimeException("User already exists with email $email")
