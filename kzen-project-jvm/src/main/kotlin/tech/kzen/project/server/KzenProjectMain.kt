package tech.kzen.project.server

import tech.kzen.auto.server.kzenAutoInit
import tech.kzen.auto.server.kzenAutoMain


//---------------------------------------------------------------------------------------------------------------------
const val kzenProjectJsModuleName = "kzen-project-js"
//const val jsResourcePath = "$staticResourcePath/$jsFileName"


fun main(args: Array<String>) {
    val context = kzenAutoInit(args, kzenProjectJsModuleName)
    kzenAutoMain(context)
}