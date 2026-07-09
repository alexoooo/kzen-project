package tech.kzen.project.server

import tech.kzen.auto.server.context.BuildInfo
import tech.kzen.auto.server.kzenAutoInit
import tech.kzen.auto.server.kzenAutoMain


//---------------------------------------------------------------------------------------------------------------------
const val kzenProjectJsModuleName = "kzen-project-js"
//const val jsResourcePath = "$staticResourcePath/$jsFileName"


fun main(args: Array<String>) {
    // Reads kzen-project's own build stamp (a distinct resource name from kzen-auto-jvm's, which is on
    //  the classpath too), so the shared kzen-auto server reports the project's version, not auto's.
    val context = kzenAutoInit(args, kzenProjectJsModuleName, BuildInfo.load("/kzen-project-build.properties"))
    kzenAutoMain(context)
}