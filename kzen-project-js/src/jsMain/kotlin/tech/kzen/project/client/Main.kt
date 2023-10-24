package tech.kzen.project.client

import tech.kzen.project.client.codegen.KzenProjectJsModule
import tech.kzen.project.common.codegen.KzenProjectCommonModule


fun main() {
//    val kzenProjectCommon = js("require('kzen-project-js.js')")
//    console.log("kzenProjectCommon", kzenProjectCommon)
//    ModuleRegistry.add(kzenProjectCommon)

    KzenProjectCommonModule.register()
    KzenProjectJsModule.register()

    tech.kzen.auto.client.main()
}
