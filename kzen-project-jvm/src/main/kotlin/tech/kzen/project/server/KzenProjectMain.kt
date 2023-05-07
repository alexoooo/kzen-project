package tech.kzen.project.server

import tech.kzen.project.common.codegen.KzenProjectCommonModule
import tech.kzen.project.server.codegen.KzenProjectJvmModule
import tech.kzen.auto.server.kzenAutoInit
import tech.kzen.auto.server.kzenAutoMain


//---------------------------------------------------------------------------------------------------------------------
const val kzenProjectJsModuleName = "kzen-project-js"
//const val jsResourcePath = "$staticResourcePath/$jsFileName"


fun main(args: Array<String>) {
    kzenProjectInit()

    val context = kzenAutoInit(args, kzenProjectJsModuleName)
    kzenAutoMain(context)
}


fun kzenProjectInit() {
    KzenProjectCommonModule.register()
    KzenProjectJvmModule.register()
}