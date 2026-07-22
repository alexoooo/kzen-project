package tech.kzen.project.server

import tech.kzen.auto.server.context.BuildInfo
import tech.kzen.auto.server.kzenAutoInit
import tech.kzen.auto.server.kzenAutoMain
import tech.kzen.project.common.codegen.KzenProjectCommonModule
import tech.kzen.project.server.codegen.KzenProjectJvmModule


//---------------------------------------------------------------------------------------------------------------------
const val kzenProjectJsModuleName = "kzen-project-js"
//const val jsResourcePath = "$staticResourcePath/$jsFileName"


fun main(args: Array<String>) {
    // kzen-project's own @Reflect classes, collected per source set by KSP. Registration is additive and
    //  FQCN-keyed, so ordering against kzen-auto's own (lib -> common -> jvm, in KzenAutoContext's companion
    //  init) is immaterial — but it must happen BEFORE kzenAutoInit, which creates the graph.
    //  These two imports are also the compile-time proof that KSP emitted both modules: a source set with
    //  zero @Reflect classes emits none, and this line stops compiling. See SampleGreeting's KDoc.
    KzenProjectCommonModule.register()
    KzenProjectJvmModule.register()

    // Reads kzen-project's own build stamp (a distinct resource name from kzen-auto-jvm's, which is on
    //  the classpath too), so the shared kzen-auto server reports the project's version, not auto's.
    val context = kzenAutoInit(args, kzenProjectJsModuleName, BuildInfo.load("/kzen-project-build.properties"))
    kzenAutoMain(context)
}