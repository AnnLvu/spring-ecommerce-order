package ecommerce.utils.exception

class DuplicateProductNameException(name: String? = null) : RuntimeException(
    if (name == null) "Product already exists" else "Product with name '$name' already exists.",
)
