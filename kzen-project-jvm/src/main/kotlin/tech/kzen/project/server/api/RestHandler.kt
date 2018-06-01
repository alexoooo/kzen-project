package tech.kzen.project.server.api

import com.google.common.io.ByteStreams
import kotlinx.coroutines.experimental.runBlocking
import com.google.common.io.MoreFiles
import com.google.common.io.Resources
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import tech.kzen.project.server.notation.ClasspathNotationSourceX
import tech.kzen.project.server.notation.FallbackNotationSourceX
import tech.kzen.project.server.notation.FileNotationSourceX
import tech.kzen.project.server.notation.GradleNotationSourceX
import tech.kzen.lib.common.notation.model.ProjectPath
import tech.kzen.lib.common.notation.scan.LiteralNotationScanner
import tech.kzen.lib.common.notation.scan.NotationScanner
import tech.kzen.lib.common.util.IoUtils
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@Component
class RestHandler {
    //-----------------------------------------------------------------------------------------------------------------
    companion object {
        val classPathRoots = listOf(
                URI("classpath:/public/"))

        val resourceDirectories = listOf<Path>(
                // TODO: dynamically discover these
                // IntelliJ and typical commandline working dir is project root
                Paths.get("kzen-project-jvm/src/main/resources/public/"),
                Paths.get("kzen-project-js/build/dist/"),

                // Eclipse and Gradle default active working directory is the module
                Paths.get("src/main/resources/public/"),
                Paths.get("../kzen-project-js/build/dist/"))

        val allowedExtensions = listOf(
                "html",
                "js",
                "css",
                "ico")
    }


    //-----------------------------------------------------------------------------------------------------------------
    fun scan(serverRequest: ServerRequest): Mono<ServerResponse> {
        val notationScanner: NotationScanner = LiteralNotationScanner(listOf(
                "notation/base/kzen-base.yaml",
                "notation/auto/kzen-auto.yaml",
                "notation/project/kzen-project.yaml"))

        val projectPaths = runBlocking {
            notationScanner.scan()
        }
//        call.respondText(gson.toJson(projectPaths), ContentType.Application.Json)

        return ServerResponse
                .ok()
//                .body(Mono.just("Foo: ..."))
                .body(Mono.just(projectPaths))
    }


    //-----------------------------------------------------------------------------------------------------------------
    fun notation(serverRequest: ServerRequest): Mono<ServerResponse> {
        val notationSource = FallbackNotationSourceX(listOf(
                GradleNotationSourceX(FileNotationSourceX()),
                ClasspathNotationSourceX()))

        val notationPrefix = "/notation/"
        val requestSuffix = serverRequest.path().substring(notationPrefix.length)

        val notationPath = ProjectPath(requestSuffix)
        val notationBytes = runBlocking {
            notationSource.read(notationPath)
        }

        val notationText = IoUtils.utf8ToString(notationBytes)

        return ServerResponse
                .ok()
                .body(Mono.just(notationText))
    }


    //-----------------------------------------------------------------------------------------------------------------
    // TODO: is this secure?
    fun resource(serverRequest: ServerRequest): Mono<ServerResponse> {
        val excludingInitialSlash = serverRequest.path().substring(1)

        val resolvedPath =
                if (excludingInitialSlash == "") {
                    "index.html"
                }
                else {
                    excludingInitialSlash
                }

        val path = Paths.get(resolvedPath).normalize()

        if (! isResourceAllowed(path)) {
            return ServerResponse
                    .badRequest()
                    .build()
        }

        val bytes: ByteArray = readResource(path)
                ?: return ServerResponse
                        .notFound()
                        .build()

        return ServerResponse
                .ok()
                .body(Mono.just(bytes))
    }


    private fun isResourceAllowed(path: Path): Boolean {
        if (path.isAbsolute) {
            return false
        }

        val extension = MoreFiles.getFileExtension(path)
        return allowedExtensions.contains(extension)
    }


    private fun readResource(relativePath: Path): ByteArray? {
        for (root in resourceDirectories) {
            val candidate = root.resolve(relativePath)
            if (! Files.exists(candidate)) {
                println("%%%%% no file at: ${candidate.toAbsolutePath()}")
                continue
            }

            val body = Files.readAllBytes(candidate)
            println("%%%%% read file: ${candidate.toAbsolutePath()}")
            return body
        }

        for (root in classPathRoots) {
            val resourceLocation: URI = root.resolve(relativePath.toString())

            try {
                val resourceUrl = Resources.getResource(resourceLocation.path)
                val body = Resources.toByteArray(resourceUrl)
                println("%%%%% read resource: ${resourceLocation.path}")
                return body
            }
            catch (ignored: Exception) {
                println("%%%%% no resource at resource: $relativePath")
            }

            val loader = DefaultResourceLoader()
            try {
                val resource = loader.getResource(resourceLocation.toString())

                val body = resource.inputStream.use {
                    ByteStreams.toByteArray(it)
                }
                println("%%%%% read resource (Spring): classpath:${relativePath}")
                return body
            }
            catch (ignored: Exception) {
                println("%%%%% no resource at resource: classpath:$relativePath")
            }
        }

        println("%%%%% not read: ${relativePath}")
        return null
    }
}