package ecommerce.service

import ecommerce.dto.cartStatistics.MembersWhoAddedToCartDTO
import ecommerce.dto.cartStatistics.TopAddedProductsDTO
import ecommerce.entity.MembersWhoAddedToCart
import ecommerce.entity.TopAddedProducts
import ecommerce.repository.CartStatisticsRepository
import org.springframework.stereotype.Service

@Service
class AdminStatisticsService(
    private val cartStatisticsRepository: CartStatisticsRepository,
) {
    fun getTopAddedProducts(limit: Int = 5): List<TopAddedProductsDTO> {
        val stats = cartStatisticsRepository.getTopAddedProducts(limit)
        return stats.map { it.toDTO() }
    }

    fun getMembersWhoAddedToCart(days: Int = 7): List<MembersWhoAddedToCartDTO> {
        val stats = cartStatisticsRepository.getMembersWhoAddedToCart(days)
        return stats.map { it.toDTO() }
    }

    private fun TopAddedProducts.toDTO(): TopAddedProductsDTO {
        return TopAddedProductsDTO(
            this.name,
            this.count,
            this.createdAt,
        )
    }

    private fun MembersWhoAddedToCart.toDTO(): MembersWhoAddedToCartDTO {
        return MembersWhoAddedToCartDTO(
            this.id,
            this.name,
            this.email,
        )
    }
}
