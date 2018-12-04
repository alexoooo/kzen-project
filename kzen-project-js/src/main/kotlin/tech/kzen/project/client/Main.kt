package tech.kzen.project.client

import tech.kzen.lib.platform.client.ModuleRegistry


fun main(args: Array<String>) {
    val kzenProjectCommon = js("require('kzen-project-js.js')")
//    console.log("kzenProjectCommon", kzenProjectCommon)
    ModuleRegistry.add(kzenProjectCommon)


    tech.kzen.auto.client.main(args)
}
