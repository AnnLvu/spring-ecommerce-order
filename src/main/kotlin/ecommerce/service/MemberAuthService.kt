package ecommerce.service

import ecommerce.dto.auth.AuthTokenPayload
import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.user.UserCreateResponse
import ecommerce.dto.user.UserRequestDto
import ecommerce.enums.UserRole
import ecommerce.model.Cart
import ecommerce.model.User
import ecommerce.repository.CartRepository
import ecommerce.repository.UserRepository
import ecommerce.exception.UserAlreadyExistsException
import ecommerce.security.JwtProvider
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MemberAuthService(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val jwtProvider: JwtProvider,
    private val loginService: LoginService,
) {
    fun signUp(userRequestDto: UserRequestDto): UserCreateResponse {
        if (userRepository.existsByEmail(userRequestDto.email)) {
            throw UserAlreadyExistsException(userRequestDto.email)
        }
        val member =
            User(
                userRequestDto.email,
                userRequestDto.password,
                userRequestDto.name,
                UserRole.USER,
            )

        member.cart = cartRepository.save(Cart())

        val savedMember = userRepository.save(member)

        val authTokenPayload = jwtProvider.createToken(AuthTokenPayload(member.email))
        return UserCreateResponse(URI.create("/users/$savedMember.id"), "Bearer $authTokenPayload")
    }

    fun login(loginRequest: LoginRequest): String {
        return loginService.login(loginRequest)
    }
}
