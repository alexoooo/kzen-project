# kzen-project
Office automation project

Dev mode (one process for client refresh, and one server process from IDE):

1) Run KzenProjectMain from IDE: --server.port=8081
    to start https://localhost:8081
    
2) Run from terminal: `gradlew -t :kzen-project-js:run`
    to run client proxy at https://localhost:8080 with live reload
    - Web UI JavaScript will be provided by webpack          
    - Everything expect `*.js` files is served by port 8081


Dist:
> ./gradlew build
>
> java -jar kzen-project-jvm/build/libs/kzen-project-jvm-*.jar

Web:
> http://localhost:8080/

Archetype zip (main.jar + dependencies/ + seed notation, into build/dist/):
> ./gradlew :kzen-project-jvm:dist


## Extending the template

kzen-project is where you add your own automation objects on top of kzen-auto. The template ships
three tiny samples, one per source set, that show each extension axis end-to-end — copy the one that
fits:

- **`SampleGreeting`** (`kzen-project-common`) — a shared `@Reflect` `DetachedAction`, declared as an
  `is: Prototype` so it appears in a Custom document's "+ Add" picker.
- **`SampleUppercaseStep`** (`kzen-project-jvm`) — your own Script step, declared as a step archetype
  + `RibbonTool` so it appears in the Script editor's palette and runs with the default step display.
- **`SampleUppercaseSummaryView`** (`kzen-project-js`) — a client-side collapsed-card `AttributeView`.

To add your own: write an `@Reflect` class, declare it in bundled notation under
`kzen-project-jvm/src/main/resources/notation/{auto-common,auto-jvm,auto-js}/` (globally-unique object
name), and the rest is automatic — KSP collects the class into that source set's generated
`KzenProject*Module`, which `KzenProjectMain` / the JS `Main` already register. No edit to kzen-auto is
needed. See each sample's KDoc and the extension-point section of `AGENTS.md` for the load-bearing
details (keep ≥ 1 `@Reflect` class per source set; the `auto-*` nesting rule).

This is the **static** (compile-time) extension mechanism. A **dynamic** plugin-JAR registration cousin
is a pending design decision, not yet available.