package minseok.kafkaplayground.payment

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentServiceApplicationTest {
    @Test
    fun shouldLoadContext() {
        assertNotNull(Unit)
    }
}
