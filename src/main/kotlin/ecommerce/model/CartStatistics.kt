package ecommerce.model

import ecommerce.enums.CartAction
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "cart_statistics")
class CartStatistics(
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    var action: CartAction,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
