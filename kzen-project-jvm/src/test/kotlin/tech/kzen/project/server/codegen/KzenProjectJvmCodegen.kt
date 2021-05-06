package tech.kzen.project.server.codegen

import tech.kzen.lib.platform.ClassName
import tech.kzen.lib.server.codegen.ModuleReflectionGenerator
import java.nio.file.Paths


object KzenProjectJvmCodegen {
    @JvmStatic
    fun main(args: Array<String>) {
        ModuleReflectionGenerator.generate(
            Paths.get("kzen-project-jvm/src/main/kotlin"),
            ClassName("tech.kzen.project.server.codegen.KzenProjectJvmModule"),
            KzenProjectCommonCodegen.commonSourceDir)
    }
}