package tech.kzen.project.client

import tech.kzen.project.client.codegen.KzenProjectJsModule
import tech.kzen.project.common.codegen.KzenProjectCommonModule


fun main() {
    // kzen-project's own @Reflect classes, collected per source set by KSP. Must run before delegating:
    //  kzen-auto's client creates the graph inside window.onload, and an unregistered class surfaces there
    //  as a bare instantiation failure. The imports are also the compile-time proof that KSP emitted both
    //  modules — see SampleGreeting's KDoc.
    KzenProjectCommonModule.register()
    KzenProjectJsModule.register()
    console.log("kzen-project modules registered: KzenProjectCommonModule, KzenProjectJsModule")

    tech.kzen.auto.client.main()
}
