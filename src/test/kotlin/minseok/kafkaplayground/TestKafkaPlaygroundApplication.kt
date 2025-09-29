package minseok.kafkaplayground

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<KafkaPlaygroundApplication>().with(TestcontainersConfiguration::class)
        .run(*args)
}
