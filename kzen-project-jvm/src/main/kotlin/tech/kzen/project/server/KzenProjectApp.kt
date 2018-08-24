package tech.kzen.project.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@SpringBootApplication
@ComponentScan(basePackages = ["tech.kzen.auto.server"])
//@ComponentScan(basePackageClasses = [tech.kzen.auto.server.KzenAutoApp.class])
class KzenProjectApp


fun main(args: Array<String>) {
    runApplication<KzenProjectApp>(*args)
}
