package tech.kzen.project.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@SpringBootApplication
class KzenProjectApp


fun main(args: Array<String>) {
    runApplication<KzenProjectApp>(*args)
}
