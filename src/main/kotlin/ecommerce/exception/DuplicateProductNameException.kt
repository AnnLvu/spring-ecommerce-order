package ecommerce.exception

class DuplicateProductNameException(name: String) : RuntimeException("Product with name '$name' already exists.")
