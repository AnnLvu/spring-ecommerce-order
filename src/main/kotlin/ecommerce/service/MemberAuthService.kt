package ecommerce.service

import ecommerce.dto.auth.AuthTokenPayload
import ecommerce.dto.auth.LoginRequest
import ecommerce.dto.user.UserCreateResponse
import ecommerce.dto.user.UserRequestDTO
import ecommerce.entity.Cart
import ecommerce.entity.User
import ecommerce.enums.UserRole
import ecommerce.exception.UserAlreadyExistsException
import ecommerce.infrastructure.JwtProvider
import ecommerce.repository.UserRepository
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MemberAuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val loginService: LoginService,
) {
    fun signUp(userRequestDTO: UserRequestDTO): UserCreateResponse {
        if (userRepository.existsByEmail(userRequestDTO.email)) {
            throw UserAlreadyExistsException(userRequestDTO.email)
        }
        val member =
            User(
                email = userRequestDTO.email,
                password = userRequestDTO.password,
                name = userRequestDTO.name,
                role = UserRole.USER,
            )

        member.cart = Cart(user = member)

        val savedMember = userRepository.save(member)

        val authTokenPayload = jwtProvider.createToken(AuthTokenPayload(member.email))
        return UserCreateResponse(URI.create("/users/$savedMember.id"), "Bearer $authTokenPayload")
    }

    fun login(loginRequest: LoginRequest): String {
        return loginService.login(loginRequest)
    }
}
