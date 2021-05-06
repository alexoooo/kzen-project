package tech.kzen.project.server.codegen

import tech.kzen.lib.platform.ClassName
import tech.kzen.lib.server.codegen.ModuleReflectionGenerator
import java.nio.file.Paths


object KzenProjectCommonCodegen {
    val commonSourceDir = Paths.get("kzen-project-common/src/commonMain/kotlin")

    @JvmStatic
    fun main(args: Array<String>) {
        ModuleReflectionGenerator.generate(
            commonSourceDir,
            ClassName("tech.kzen.project.common.codegen.KzenProjectCommonModule"))
    }
}