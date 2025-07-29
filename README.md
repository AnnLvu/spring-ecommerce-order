# spring-ecommerce-product

## Controller
### Admin
#### AdminProductController
- [x] `GET /api/admin/products` all products
- [x] `GET /api/admin/products/:id` product by ID
- [x] `POST /api/admin/products` Create a Product
- [x] `PUT /api/admin/products/:id` update the whole product by ID
- [x] `PATCH /api/admin/products/:id` update one or more attributes of Product by ID
- [x] `DELETE /api/admin/products/:id` delete the product by ID
#### AdminAuthController
- [x] `POST /api/admin/auth/signIn`

### Guest
#### GuestProductController
- [x] `GET /api/products` all products

### Member
#### AuthController
- [x] `POST /api/member/auth/signUp` creates user and returns JWT token
- [x] `POST /api/member/auth/signIn` checks and returns JWT token
#### CartController
- [x] `GET /api/member/cart` getCartProducts
- [x] `POST /api/member/cart/:id` incrementCartProduct
- [x] `DELETE /api/member/cart/:id` decrementCartProduct

## Config
### AuthInterceptor
### LoginMemberArgumentResolver
### WebConfig

## Service
### AdminAuthService
- [x] `signIn(loginRequest: LoginRequest)`: String
### AdminProductService
- [x] `getAllProducts()`: List<ProductDTO>
- [x] `getProductById(id: Long)`: ProductDTO
- [x] `createProduct(product: ProductDTO):`  Void
- [x] `updateProduct(id: Long, product: ProductDTO)`: Void
- [x] `fun deleteProduct(id: Long)`: Void
### AdminStatisticsService
- [x] `getTopAddedProducts(limit: Int = 5)`: List<TopAddedProductsDTO>
- [x] `getMembersWhoAddedToCart(days: Int = 7)`: List<MembersWhoAddedToCartDTO>
### CartService
- [x] `getCartProducts(userID: Long?, productID: Long)`: List<CartProductResponseDTO>
- [x] `addProductToCart(userID: Long?, productID: Long)`: Long
- [x] `removeProductFromCart(userID: Long?, productID: Long)`: Void
### LoginService
- [x] `fun signIn(loginRequest: LoginRequest, expectedRole: UserRole = UserRole.USER): String`: String
### MemberAuthService
- [x] `signUp(user: UserRequestDTO)`: UserCreateResponse
- [x] `fun logIn(loginRequest: LoginRequest)`: String

## Repository
### CartProductRepository
- `getCartProducts(cartID: Long)`: List<CartProductResponseDTO>
- `findCartProduct(cartID: Long, productID: Long)`: CartProductDTO?
- `updateProductQuantity(cartProductId: Long, quantity: Int)`: Int
- `removeProduct(cartID: Long, productID: Long)`: Int
- `addProduct(cartID: Long, productID: Long)`: Long
### CartRepository
- `createCartForUser(userId: Long)`: Void
- `findMembersCart(userId: Long?)`: CartDTO?
### CartStatisticsRepository
- `create(userID: Long?, productID: Long, action: CartAction)`: Long
- `getTopAddedProducts(limit: Int = 5)`: List<TopAddedProductsDTO>
- `getMembersWhoAddedToCart(days: Int = 7)`: List<MembersWhoAddedToCartDTO>
### ProductRepository
- `findAll()`: List<ProductDTO>
- `findById(id: Long)`: ProductDTO?
- `create(product: ProductDTO)`: Long
- `findByName(name: String)`: ProductDTO?
- `existsByName(name: String)`: Boolean
- `create(product: ProductDTO)`: Long
- `update(id: Long, product: ProductDTO)`: Int
- `deleteById(id: Long)`: Int
### UserRepository
- `create(user: UserDTO)`: Long
- `findByEmailAndPassword(email: String, password: String)`: UserDTO?
- `findByEmail(email: String)`: UserDTO?
- `existsByEmail(email: String)`: Boolean

## DTO
### Auth
#### AuthTokenPayload
- email: String
#### LoginRequest
- email: String
- password: String
### Cart
#### CartDTO
- id: Long
- userId: Long
### CartProduct
#### CartProductDTO
- id: Long
- cartId: Long
- productID: Long
- quantity: Int
### CartStatistics
#### MembersWhoAddedToCartDTO
- id: Long
- name: String
- email: String
#### TopAddedProductsDTO
- name: String
- count: Int
- createdAt: String
### Error
#### ErrorResponse
- timestamp: Instant = Instant.now()
- status: Int
- error: String
- message: Any
- path: String? = null
### Products
#### ProductDTO
- Validation for product modal
- [x] `name`: Not blank, Maximum of 15, Minimum of 1, starts with http or https
- [x] `description`: Not blank, Minimum of 3
- [x] `price`: is Positive
- [x] `imageUrl` Not Blank, Follows pattern
- [x] `quantity` Cannot be negative (0 included)
#### ProductPatchDTO
- Same as `ProductDTO` but allowed null
### User
#### UserRequestDTO
- Validation for user Modal
- [x] `email`: Not blank and should be email
- [x] `password`: Not blank and min length of 6
- [x] `name`: Not blank
- [x] `role`: should be admin or user `[Default = user]`

#### UserCreateResponse
- uri: URI
- token: String

#### UserDTO
- id: Long? = null
- email: String
- password: String
- name: String
- role: UserRole
## Infrastructure
### JwtTokenProvider
- [x] `createToken`: String
- [x] `getPayload`: AuthTokenPayload
- [x] `validateToken`: Boolean

## Mapper
### CartProductMapper
### CartProductResponseMapper
### CartRowMapper
### MembersWhoAddedToCartMapper
### ProductRowMapper
### TopAddedProductsMapper
### UserRowMapper

## Advice
### GlobalExceptionHandler
- `handleEmptyResult` -> if no element is found with the ID
- `handleHttpMessageNotReadable` -> if there is a missing field
- `handleDuplicateProductName` -> if name is already taken
- `handleValidationException` -> if there is error for validation

## exception
- `DuplicateProductNameException`
- `EntityNotFoundException`
- `UserAlreadyExistsException`
- `UserCredentialException`


## enums
### UserRoles
- [x] Admin
- [x] User

### CartActions
- [x] ADD
- [x] DELETE

## Schema
### Products
- id, name, description, price, image_url, quantity
- [x] name is `UNIQUE`

### Users
- id, email, password, name, role
- [x] email is unique

### Cart
- id
- user_id (FK)

### CartProducts
- id, quantity
- cart_id (FK)
- product_id (FK)

### CartStatistics
- id, created_at, action
- user_id, product_id (FK)


## Tests
### Controller
#### Admin
- [x] AdminCartStatisticsControllerTest
- [x] AdminProductControllerTest
#### Guest
- [x] GuestProductControllerTest
#### Member
- [x] CartControllerTest
- [x] MemberAuthControllerTest
### DTO
- [x] ProductDTOTest
- [x] UserRequestDTOTest
### Repository
- [x] CartRepositoryTest
- [x] CartStatisticsRepositoryTest
- [x] ProductRepositoryTest
- [x] UserRepositoryTest
### Service
- [x] CartServiceTest
- [x] MemberAuthServiceTest
- [x] ProductServiceTest
