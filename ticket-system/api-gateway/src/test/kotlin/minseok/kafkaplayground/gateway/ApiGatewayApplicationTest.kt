package minseok.kafkaplayground.gateway

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApiGatewayApplicationTest {
    @Test
    fun shouldLoadContext() {
        assertNotNull(Unit)
    }
}
