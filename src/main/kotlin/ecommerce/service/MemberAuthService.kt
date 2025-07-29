package ecommerce.service

import ecommerce.dto.auth.AuthTokenPayload
import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.user.UserCreateResponse
import ecommerce.dto.user.UserRequestDTO
import ecommerce.entity.User
import ecommerce.enums.UserRole
import ecommerce.exception.UserAlreadyExistsException
import ecommerce.infrastructure.JwtProvider
import ecommerce.repository.CartRepository
import ecommerce.repository.UserRepository
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MemberAuthService(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val jwtProvider: JwtProvider,
    private val loginService: LoginService,
) {
    fun signUp(user: UserRequestDTO): UserCreateResponse {
        if (userRepository.existsByEmail(user.email)) {
            throw UserAlreadyExistsException(user.email)
        }
        val member =
            User(
                email = user.email,
                password = user.password,
                name = user.name,
                role = UserRole.USER,
            )
        val id = userRepository.create(member)
        cartRepository.createCartForUser(id)
        val authTokenPayload = jwtProvider.createToken(AuthTokenPayload(member.email))
        return UserCreateResponse(URI.create("/users/$id"), "Bearer $authTokenPayload")
    }

    fun logIn(loginRequest: LoginRequest): String {
        return loginService.login(loginRequest)
    }
}
