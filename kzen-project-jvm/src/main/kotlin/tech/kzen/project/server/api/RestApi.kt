package tech.kzen.project.server.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router


@Configuration
class RestApi(
        private val counterHandler: RestHandler
) {
    @Bean
    fun counterRouter() = router {
        GET("/scan", counterHandler::scan)

        GET("/notation/**", counterHandler::notation)

        GET("/", counterHandler::resource)
        GET("/**", counterHandler::resource)
    }
}