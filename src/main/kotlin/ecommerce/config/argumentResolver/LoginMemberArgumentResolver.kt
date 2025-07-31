package ecommerce.config.argumentResolver

import ecommerce.enums.UserRole
import ecommerce.model.User
import ecommerce.repository.UserRepository
import ecommerce.utils.annotations.LoginMember
import ecommerce.utils.exception.UnauthorisedUserException
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class LoginMemberArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginMember::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User {
        val request = (webRequest as ServletWebRequest).request
        val email = request.getAttribute("email") as String

        val user =
            userRepository.findByEmail(email).orElseThrow { UnauthorisedUserException("User not found") }
        if (user.role != UserRole.USER) {
            throw UnauthorisedUserException("User role not valid")
        }

        return user
    }
}
