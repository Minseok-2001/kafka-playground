package minseok.kafkaplayground.payment.domain

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentTransactionRepository : JpaRepository<PaymentTransaction, Long>
