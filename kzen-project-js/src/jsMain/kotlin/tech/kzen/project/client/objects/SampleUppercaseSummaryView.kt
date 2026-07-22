package tech.kzen.project.client.objects

import emotion.react.css
import react.ChildrenBuilder
import react.State
import react.dom.html.ReactHTML.div
import tech.kzen.auto.client.objects.document.common.attribute.AttributeView
import tech.kzen.auto.client.objects.document.common.attribute.AttributeViewProps
import tech.kzen.auto.client.service.global.ClientState
import tech.kzen.auto.client.service.global.ClientStateGlobal
import tech.kzen.auto.client.wrap.RPureComponent
import tech.kzen.auto.client.wrap.react
import tech.kzen.auto.client.wrap.setState
import tech.kzen.lib.common.model.attribute.AttributePath
import tech.kzen.lib.common.model.location.ObjectReference
import tech.kzen.lib.common.model.location.ObjectLocation
import tech.kzen.lib.common.model.location.ObjectReferenceHost
import tech.kzen.lib.common.reflect.Reflect
import tech.kzen.lib.common.reflect.Service
import web.cssom.*


//---------------------------------------------------------------------------------------------------------------------
external interface SampleUppercaseSummaryViewState: State {
    var text: String?
}


//---------------------------------------------------------------------------------------------------------------------
/**
 * Sample extension #3 of 3: a client-side display extension — the collapsed-card summary for
 * [SampleUppercaseStep][tech.kzen.project.server.objects.SampleUppercaseStep], rendering
 * "→ UPPERCASE(ReferencedStep)" under the step's title when its card is collapsed.
 *
 * ## To add your own
 *
 * 1. Implement an [AttributeView] `Wrapper` (the `@Reflect` entry point, which the graph
 *    instantiates) around an [RPureComponent] in `kzen-project-js/src/jsMain/kotlin`.
 * 2. Declare `is: AttributeView` in `notation/auto-js/` with `class:` naming the `$Wrapper` inner
 *    class (see `auto-js/kzen-project/sample-js.yaml`).
 * 3. Point at it from the owning archetype's attribute metadata with a `summary:` tag — here
 *    `meta.input.summary: SampleUppercaseSummaryView` in `auto-jvm/kzen-project/sample-jvm.yaml`.
 *
 * This is the right-sized client extension axis: a step inherits `ScriptStepDisplayDefault` for the
 * whole card (run / park / trace / expand all work with no JS at all), and an `AttributeView`
 * customizes just the part that benefits from it.
 *
 * ## Load-bearing
 *
 * The only `@Reflect` class in `kzen-project-js` — see
 * [SampleGreeting][tech.kzen.project.common.objects.SampleGreeting] for why deleting it would break
 * the JS entry point's compile.
 */
@Suppress("unused")
class SampleUppercaseSummaryView(
    props: AttributeViewProps
):
    RPureComponent<AttributeViewProps, SampleUppercaseSummaryViewState>(props),
    ClientStateGlobal.Observer
{
    //-----------------------------------------------------------------------------------------------------------------
    @Reflect
    class Wrapper(
        objectLocation: ObjectLocation,
        @Service private val clientStateGlobal: ClientStateGlobal
    ):
        AttributeView(objectLocation)
    {
        override fun ChildrenBuilder.child(block: AttributeViewProps.() -> Unit) {
            SampleUppercaseSummaryView::class.react {
                clientStateGlobal = this@Wrapper.clientStateGlobal
                block()
            }
        }
    }


    //-----------------------------------------------------------------------------------------------------------------
    companion object {
        private val inputAttributePath = AttributePath.parse("input")
    }


    //-----------------------------------------------------------------------------------------------------------------
    override fun componentDidMount() {
        props.clientStateGlobal.observe(this)
    }


    override fun componentWillUnmount() {
        props.clientStateGlobal.unobserve(this)
    }


    override fun onClientState(clientState: ClientState) {
        val graphNotation = clientState.graphStructure().graphNotation

        if (props.objectLocation !in graphNotation.coalesce) {
            // NB: containing step was renamed or deleted; parent re-render will swap props.objectLocation shortly
            return
        }

        // The referenced step's NAME, not the document-qualified reference a plain
        //  ReferenceLinkAttributeView would show.
        val inputName = graphNotation
            .firstAttribute(props.objectLocation, inputAttributePath)
            ?.asString()
            ?.takeIf { it.isNotEmpty() }
            ?.let { ObjectReference.parse(it) }
            ?.let { graphNotation.coalesce.locateOptional(it, ObjectReferenceHost.ofLocation(props.objectLocation)) }
            ?.objectPath
            ?.name
            ?.value

        val text = inputName?.let { "→ UPPERCASE($it)" }

        if (state.text == text) {
            return
        }

        setState {
            this.text = text
        }
    }


    //-----------------------------------------------------------------------------------------------------------------
    override fun ChildrenBuilder.render() {
        val text = state.text
            ?: return

        div {
            css {
                color = Color("rgba(0, 0, 0, 0.55)")
                fontSize = 0.85.em
                whiteSpace = WhiteSpace.nowrap
                overflow = Overflow.hidden
                textOverflow = TextOverflow.ellipsis
                minWidth = 0.px
            }

            +text
        }
    }
}
