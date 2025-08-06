package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name = "orders")
class Order(
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "option_id")
    val productOption: Option,

    var quantity: Int,

    @Column(name = "stripe_session_id", nullable = false)
    var stripeSessionId: String,

    @Column(nullable = false)
    var amount: Double,

    @Column(nullable = false)
    var status: String = "PENDING",


    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)