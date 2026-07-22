package tech.kzen.project.server

import kotlinx.coroutines.runBlocking
import tech.kzen.auto.server.context.KzenAutoContext
import tech.kzen.auto.server.exec.LogicCompiler
import tech.kzen.auto.server.exec.LogicCompilerServices
import tech.kzen.lib.common.exec.ExecutionRequest
import tech.kzen.lib.common.exec.ExecutionSuccess
import tech.kzen.lib.common.exec.RequestParams
import tech.kzen.lib.common.exec.engine.Outcome
import tech.kzen.lib.common.exec.logic.run.model.LogicRunExecutionId
import tech.kzen.lib.common.exec.tuple.TupleValue
import tech.kzen.lib.common.model.document.DocumentPath
import tech.kzen.lib.common.model.location.ObjectLocation
import tech.kzen.lib.common.model.obj.ObjectPath
import tech.kzen.lib.server.exec.engine.RunEngine
import tech.kzen.project.common.codegen.KzenProjectCommonModule
import tech.kzen.project.server.codegen.KzenProjectJvmModule
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue


/**
 * The extension-point acceptance test: kzen-project's own `@Reflect` classes reach the graph through the
 * KSP-generated modules the entry points register, and its bundled notation is discovered off the classpath —
 * both with zero kzen-auto edits. This is what a downstream user adding their own `@Reflect` class relies on,
 * so a regression here means the template's extension point silently stopped working.
 *
 * The three tests cover the three extension axes end-to-end on the JVM:
 *
 * - [bundledDocsDiscoveredOnClasspath] pins classpath discovery AND the document-nesting choice — the
 *   instantiation choke points filter by `AutoConventions.serverAllowed` / `clientUiAllowed`, which are fixed
 *   sets, so bundled documents must sit under `auto-common/` / `auto-jvm/` / `auto-js/`.
 * - [sampleGreetingExecutesThroughDetachedPath] instantiates
 *   [SampleGreeting][tech.kzen.project.common.objects.SampleGreeting] through the full notation → definition →
 *   `GraphCreator` path inside `ModelDetachedExecutor` (including its `serverAllowed` filter).
 * - [sampleStepRunsInScript] compiles and runs
 *   [SampleUppercaseStep][tech.kzen.project.server.objects.SampleUppercaseStep] on the real `RunEngine`.
 *
 * The JS axis ([SampleUppercaseSummaryView][tech.kzen.project.client.objects.SampleUppercaseSummaryView]) has
 * no JVM counterpart; its proof is that `KzenProjectJsModule` compiles in the JS entry point plus the browser
 * smoke listed in the SH4 plan's verification section.
 */
class SampleExtensionTest {
    //-----------------------------------------------------------------------------------------------------------------
    private lateinit var context: KzenAutoContext


    @AfterTest
    fun tearDown() {
        if (::context.isInitialized) {
            context.close()
        }
    }


    // Registration is additive, FQCN-keyed and idempotent, exactly as in KzenProjectMain — every test needs
    //  it because the graph is built per test.
    private fun createContext(): KzenAutoContext {
        KzenProjectCommonModule.register()
        KzenProjectJvmModule.register()
        context = KzenAutoContext.forTest()
        return context
    }


    //-----------------------------------------------------------------------------------------------------------------
    @Test
    fun bundledDocsDiscoveredOnClasspath() {
        val documentPaths = runBlocking { createContext().graphStore.graphNotation() }
            .documents
            .map
            .keys

        for (bundled in listOf(
            "auto-common/kzen-project/sample-common.yaml",
            "auto-jvm/kzen-project/sample-jvm.yaml",
            "auto-js/kzen-project/sample-js.yaml"
        )) {
            assertTrue(
                DocumentPath.parse(bundled) in documentPaths,
                "bundled sample document not discovered: $bundled")
        }
    }


    @Test
    fun sampleGreetingExecutesThroughDetachedPath() {
        val instanceLocation = ObjectLocation(
            DocumentPath.parse("auto-common/kzen-project/sample-greeting-test.yaml"),
            ObjectPath.parse("SampleGreetingTestInstance"))

        val result = runBlocking {
            createContext().detachedExecutor.execute(
                instanceLocation, ExecutionRequest(RequestParams.empty, null))
        }

        assertEquals("Hello from the sample", assertIs<ExecutionSuccess>(result).value.get())
    }


    @Test
    fun sampleStepRunsInScript() {
        val outcome = runScript("test/sample-step-test.yaml")
        assertEquals("HELLO", assertIs<Outcome.Success>(outcome).value.mainComponentValue())
    }


    //-----------------------------------------------------------------------------------------------------------------
    private fun runScript(documentPathString: String): Outcome {
        val context = createContext()

        val scriptLocation = ObjectLocation(
            DocumentPath.parse(documentPathString),
            ObjectPath.parse("main"))

        val graphNotation = runBlocking { context.graphStore.graphNotation() }
        val graphDefinition = runBlocking { context.graphStore.graphDefinition() }.transitiveSuccessful

        val logic = LogicCompiler.compile(
            scriptLocation,
            graphNotation,
            graphDefinition,
            compilerServices())

        val engine = RunEngine(
            logic, context.objectStableMapper.objectStableId(scriptLocation), TupleValue.empty)

        return try {
            runBlocking {
                engine.resume()
                engine.await()
            }
        }
        finally {
            engine.close()
        }
    }


    private fun compilerServices(): LogicCompilerServices {
        return LogicCompilerServices(
            context.graphEnvironment,
            context.objectStableMapper,
            context.cachedKotlinCompiler,
            context.scriptValidationCache,
            context.notationMetadataReader,
            context.jobWorkPool,
            LogicRunExecutionId.random())
    }
}
