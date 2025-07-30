package ecommerce.repository

import ecommerce.jpaEntity.CartStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartStatisticsRepository : JpaRepository<CartStatistics, Long>
