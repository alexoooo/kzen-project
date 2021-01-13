package tech.kzen.project.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.EnableWebFlux
import tech.kzen.auto.server.kzenAutoInit


@EnableWebFlux
@SpringBootApplication
@ComponentScan(basePackages = ["tech.kzen.auto.server"])
class KzenProjectMain


fun main(args: Array<String>) {
    kzenAutoInit()
    runApplication<KzenProjectMain>(*args)
}
