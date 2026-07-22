package tech.kzen.project.server.objects

import tech.kzen.auto.server.objects.script.api.ScriptStep
import tech.kzen.auto.server.objects.script.api.ScriptStepDefinition
import tech.kzen.auto.server.objects.script.api.StepExecution
import tech.kzen.auto.server.objects.script.model.ScriptDefinitionContext
import tech.kzen.lib.common.exec.logic.model.LogicType
import tech.kzen.lib.common.exec.tuple.TupleDefinition
import tech.kzen.lib.common.model.location.ObjectLocation
import tech.kzen.lib.common.reflect.Reflect


/**
 * Sample extension #2 of 3: your own Script step — the axis most downstream users want.
 * Uppercases the value produced by the step it references.
 *
 * ## To add your own
 *
 * 1. Implement [ScriptStep] in `kzen-project-jvm/src/main/kotlin` and annotate it `@Reflect`.
 *    [definition] declares the step's output shape at compile time; [run] executes it.
 * 2. Declare a step archetype in `notation/auto-jvm/` (see
 *    `auto-jvm/kzen-project/sample-jvm.yaml`): `abstract: true`, `is: ScriptStep`, `class:`, plus
 *    `title:`/`icon:` for the ribbon and a `meta:` entry per constructor parameter.
 * 3. Add an `is: RibbonTool` object in `notation/auto-js/` pointing `delegate:` at the archetype, so
 *    the step appears in the Script editor's palette (see `auto-js/kzen-project/sample-js.yaml`).
 *
 * Nothing in kzen-auto changes: [tech.kzen.auto.server.exec.script.ScriptLogicCompiler] resolves
 * steps polymorphically from notation, so there is no `when` to extend and no registry to edit.
 * The step also renders, runs, parks and traces with the default step display — a custom
 * `ScriptStepDisplay` is only needed for a genuinely bespoke card.
 *
 * ## Load-bearing
 *
 * The only `@Reflect` class in `kzen-project-jvm` — see [tech.kzen.project.common.objects.SampleGreeting]
 * for why deleting it would break the JVM entry point's compile.
 */
@Reflect
class SampleUppercaseStep(
    private val input: ObjectLocation
):
    ScriptStep
{
    override fun definition(scriptDefinitionContext: ScriptDefinitionContext): ScriptStepDefinition {
        return ScriptStepDefinition.of(
            TupleDefinition.ofMain(LogicType.string))
    }


    override suspend fun run(execution: StepExecution): Any? {
        val value = execution.referencedValue(input)?.toString() ?: ""
        val uppercase = value.uppercase()
        execution.traceDetail(uppercase)
        return uppercase
    }
}
