package tech.kzen.project.server.codegen

import tech.kzen.lib.platform.ClassName
import tech.kzen.lib.server.codegen.ModuleReflectionGenerator
import java.nio.file.Paths


object KzenProjectJsCodegen {
    @JvmStatic
    fun main(args: Array<String>) {
        ModuleReflectionGenerator.generate(
            Paths.get("kzen-project-js/src/main/kotlin"),
            ClassName("tech.kzen.project.client.codegen.KzenProjectJsModule"),
            KzenProjectCommonCodegen.commonSourceDir)
    }
}