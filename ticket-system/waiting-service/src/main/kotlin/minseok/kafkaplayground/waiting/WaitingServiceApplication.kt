package minseok.kafkaplayground.waiting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WaitingServiceApplication

fun main(args: Array<String>) {
    runApplication<WaitingServiceApplication>(*args)
}
