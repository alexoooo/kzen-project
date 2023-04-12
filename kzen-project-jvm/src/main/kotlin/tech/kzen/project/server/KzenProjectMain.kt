package tech.kzen.project.server

import tech.kzen.project.common.codegen.KzenProjectCommonModule
import tech.kzen.project.server.codegen.KzenProjectJvmModule
import tech.kzen.auto.server.KzenAutoConfig
import tech.kzen.auto.server.kzenAutoMain


//---------------------------------------------------------------------------------------------------------------------
const val kzenProjectJsModuleName = "kzen-project-js"
//const val jsResourcePath = "$staticResourcePath/$jsFileName"


fun main(args: Array<String>) {
    kzenProjectInit()

    kzenAutoMain(KzenAutoConfig(
        jsModuleName = kzenProjectJsModuleName,
        port = 8080,
        host = "127.0.0.1"
    ))
}


fun kzenProjectInit() {
    KzenProjectCommonModule.register()
    KzenProjectJvmModule.register()
}