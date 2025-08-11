package ecommerce.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig(
    @Value("\${app.cors.allowed-origins:*}") private val allowedOriginsProp: String,
) {
    @Bean
    fun corsFilter(): CorsFilter {
        val cfg =
            CorsConfiguration().apply {
                allowCredentials = false

                val tokens = allowedOriginsProp.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                if (tokens.size == 1 && tokens[0] == "*") {
                    addAllowedOriginPattern("*")
                } else {
                    allowedOrigins = tokens
                }

                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("*")
                exposedHeaders = listOf(HttpHeaders.LOCATION) // тест ждёт этот заголовок
                maxAge = 1800
            }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cfg)
        return CorsFilter(source)
    }
}
