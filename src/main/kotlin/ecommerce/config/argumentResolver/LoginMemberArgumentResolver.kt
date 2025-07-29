package ecommerce.config.argumentResolver

import ecommerce.annotations.LoginMember
import ecommerce.dto.user.MemberUser
import ecommerce.enums.UserRole
import ecommerce.exception.UnauthorisedUserException
import ecommerce.repository.UserRepository
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
    ): MemberUser {
        val request = (webRequest as ServletWebRequest).request
        val email = request.getAttribute("email") as String

        val user =
            userRepository.findByEmail(email)
                ?: throw UnauthorisedUserException("User not found")

        if (user.role != UserRole.USER) {
            throw UnauthorisedUserException("User role not valid")
        }

        return MemberUser(
            user.id,
            user.email,
            user.name,
        )
    }
}
