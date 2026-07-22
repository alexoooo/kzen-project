package tech.kzen.project.common.objects

import tech.kzen.auto.common.paradigm.detached.DetachedAction
import tech.kzen.lib.common.exec.ExecutionRequest
import tech.kzen.lib.common.exec.ExecutionResult
import tech.kzen.lib.common.exec.ExecutionValue
import tech.kzen.lib.common.reflect.Reflect


/**
 * Sample extension #1 of 3: a shared (multiplatform) notation object — the smallest executable thing
 * kzen-project can add, and the template for your own.
 *
 * ## To add your own
 *
 * 1. Write a class in `kzen-project-common/src/commonMain` (or `jvmMain`/`jsMain`) and annotate it
 *    `@Reflect`. Constructor parameters are filled from the object's notation attributes by name;
 *    `@Service` parameters are filled from the server's `GraphEnvironment`.
 * 2. Declare it in notation under `kzen-project-jvm/src/main/resources/notation/auto-common/`, with
 *    `class:` naming the fully-qualified class and one entry per constructor parameter — see
 *    `auto-common/kzen-project/sample-common.yaml`, which declares this class as an `is: Prototype`
 *    so it shows up in a Custom document's "+ Add" picker.
 * 3. Nothing else. KSP collects every `@Reflect` class in the source set into
 *    `tech.kzen.project.common.codegen.KzenProjectCommonModule`, which both entry points already
 *    register (`KzenProjectMain` and the JS `Main`).
 *
 * ## Load-bearing
 *
 * This class is the ONLY `@Reflect` class in `kzen-project-common`. The KSP processor deliberately
 * emits no module object for a source set with zero `@Reflect` classes, so deleting this file
 * un-emits `KzenProjectCommonModule` and breaks both entry points' compile — by design, a loud
 * failure rather than a silent runtime one. Keep at least one `@Reflect` class per source set.
 *
 * @see tech.kzen.project.server.objects.SampleUppercaseStep for the JVM (automation step) axis
 */
@Reflect
class SampleGreeting(
    private val message: String
):
    DetachedAction
{
    override suspend fun execute(
        request: ExecutionRequest
    ): ExecutionResult {
        return ExecutionResult.success(
            ExecutionValue.of(message))
    }
}
