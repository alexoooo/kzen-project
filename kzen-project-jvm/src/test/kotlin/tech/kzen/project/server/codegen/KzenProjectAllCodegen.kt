package tech.kzen.project.server.codegen


object KzenProjectAllCodegen {
    @JvmStatic
    fun main(args: Array<String>) {
        KzenProjectCommonCodegen.main(args)
        KzenProjectJsCodegen.main(args)
        KzenProjectJvmCodegen.main(args)
    }
}