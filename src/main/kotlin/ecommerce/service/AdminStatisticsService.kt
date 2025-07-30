package ecommerce.service

import ecommerce.dto.cartStatistics.MembersWhoAddedToCartDTO
import ecommerce.dto.cartStatistics.TopAddedProductsDTO
import ecommerce.enums.CartAction
import ecommerce.repository.CartStatisticsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AdminStatisticsService(
    private val cartStatisticsRepository: CartStatisticsRepository,
) {
    fun getTopAddedProducts(): List<TopAddedProductsDTO> {
        val since = LocalDateTime.now().minusDays(TOP_PRODUCTS_SINCE)
        val limit = PageRequest.of(0, TOP_PRODUCTS_LIMIT)
        return cartStatisticsRepository.findTopProducts(CartAction.ADD, since, limit)
    }

    fun getMembersWhoAddedToCart(): List<MembersWhoAddedToCartDTO> {
        val since = LocalDateTime.now().minusDays(ACTIVE_USERS_SINCE)
        return cartStatisticsRepository.findActiveUsersSince(since)
    }

    companion object {
        private const val TOP_PRODUCTS_SINCE = 30L
        private const val TOP_PRODUCTS_LIMIT = 5
        private const val ACTIVE_USERS_SINCE = 7L
    }
}
