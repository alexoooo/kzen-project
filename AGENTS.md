# kzen-project — AI agent guide

## Purpose

kzen-project is the **office-automation product** built on top of kzen-auto. It's a thin shell that boots kzen-auto's server as-is and packages the result as a distributable. Most of the heavy lifting (UI, paradigms, plugin SPI) lives in `../kzen-auto`; this repo carries a small set of **sample `@Reflect` objects** (one per source set) that double as the living "how to extend the template" documentation (see the extension-point section below).

Read [`../kzen-lib/docs/architecture.md`](../kzen-lib/docs/architecture.md) and [`../kzen-auto/AGENTS.md`](../kzen-auto/AGENTS.md) before working here — kzen-project assumes you understand both.

## Module layout

Three Gradle subprojects (KMP shape, no plugin module):

- **`kzen-project-common`** — KMP shared code (`commonMain`/`commonTest`). Project-specific notation objects and shared models. Currently lean (the bulk of common logic is in kzen-auto-common).
- **`kzen-project-jvm`** — JVM server. Entry point `tech.kzen.project.server.KzenProjectMain` (`fun main`) which delegates to `kzenAutoInit` + `kzenAutoMain` from kzen-auto-jvm.
- **`kzen-project-js`** — Kotlin/JS frontend. Entry point `tech.kzen.project.client.Main`.

## Entry points

| Class | Module | Purpose |
|----|----|----|
| `tech.kzen.project.server.KzenProjectMain` | kzen-project-jvm | Production server `fun main`. Registers `KzenProjectCommonModule` + `KzenProjectJvmModule` (its own KSP-generated `@Reflect` modules), then delegates to `kzenAutoInit` + `kzenAutoMain`. |
| `tech.kzen.project.client.Main` | kzen-project-js | JS entry point. Registers `KzenProjectCommonModule` + `KzenProjectJsModule`, logs it, then delegates to `tech.kzen.auto.client.main()`. |

## Dev loop

Dual-server pattern: backend on `:8081`, webpack proxy on `:8080` with live JS reload. **Open kzen-project as its OWN IntelliJ project** (umbrella's KMP includeBuild breaks IDE run/debug — see umbrella AGENTS.md).

```powershell
# Terminal 1 — IDE: run tech.kzen.project.server.KzenProjectMain with program args:
#     --server.port=8081
# This serves the backend (and everything except *.js) at http://localhost:8081

# Terminal 2 — webpack proxy + live JS reload at http://localhost:8080:
./gradlew -t :kzen-project-js:run

# Browse: http://localhost:8080/
```

Webpack at `:8080` proxies non-`*.js` requests to `:8081`; only JS bundles are served from the webpack dev server, which is what enables HMR.

## Distribution build

```powershell
./gradlew build
java -jar kzen-project-jvm/build/libs/kzen-project-jvm-*.jar
```

The `main.jar` inside `kzen-project-<v>.zip` is what kzen-shell ultimately spawns at runtime. The `:kzen-project-jvm:dist` Gradle `Zip` task builds that zip — thin `main.jar` (`Class-Path` → `dependencies/`) + `dependencies/` + the loose seed notation under `src/main/resources/notation/main/` — into `build/dist/`. (Not wired into `build`.)

## Key directories

| Path | What lives here |
|----|----|
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/server/KzenProjectMain.kt` | JVM entry point |
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/common/CommonServer.kt` | Server-side common helpers |
| `kzen-project-js/src/jsMain/kotlin/tech/kzen/project/client/Main.kt` | JS entry point |
| `kzen-project-common/src/commonMain/kotlin/tech/kzen/project/common/objects/SampleGreeting.kt` | Sample `@Reflect` `DetachedAction` (shared source set) — the Custom-prototype extension axis |
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/server/objects/SampleUppercaseStep.kt` | Sample `@Reflect` Script step (JVM) — the automation-step extension axis |
| `kzen-project-js/src/jsMain/kotlin/tech/kzen/project/client/objects/SampleUppercaseSummaryView.kt` | Sample `@Reflect` `AttributeView` (JS) — the client-display extension axis |
| `kzen-project-jvm/src/main/resources/notation/` | Bundled read-only notation declaring the samples: `auto-common/`, `auto-jvm/`, `auto-js/` (`kzen-project/` subdir). NOT `main/` — see the extension-point section |

The `KzenProjectCommonModule` / `KzenProjectJvmModule` / `KzenProjectJsModule` classes named in the three build files' KSP args are **generated into `build/`, not committed source**. KSP emits a module object only for a source set that contains ≥ 1 `@Reflect` class, which is why each source set carries exactly one sample (below).

## Gotchas

- **kzen-auto plugin publish dependency.** kzen-project depends on `kzen-auto-plugin` through the normal mavenLocal route (variant-suffix coords). After any change to `kzen-auto-plugin`, run `cd ../kzen-auto && ./gradlew :kzen-auto-plugin:publishToMavenLocal` before building kzen-project standalone.
- **The `@Reflect` extension point works — via the samples (SH4).** To add your own object: write an `@Reflect` class in the relevant source set, declare it in bundled notation, done — KSP collects it into that source set's `KzenProject*Module` and the entry point's `register()` picks it up (`KzenProjectMain` registers Common + Jvm; JS `Main` registers Common + Js). Load-bearing rules:
  - **The samples are load-bearing for compilation.** Each source set has exactly one sample `@Reflect` class (`SampleGreeting` common, `SampleUppercaseStep` jvm, `SampleUppercaseSummaryView` js). KSP suppresses the module object for a source set with zero `@Reflect` classes, so deleting a module's last `@Reflect` class un-emits its `KzenProject*Module` and breaks the corresponding main's `register()` line — a loud missing-symbol failure by design, not a bug. Keep ≥ 1 `@Reflect` class per source set.
  - **Bundled notation must live under `auto-common/`, `auto-jvm/`, or `auto-js/`** (a `kzen-project/` subdir keeps it distinguishable). The instantiation choke points filter by `AutoConventions.serverAllowed` / `clientUiAllowed` — fixed sets in kzen-auto-common — so a `project-*` nesting would need a kzen-auto change (an EXT candidate, out of scope). Object names are graph-global (hence the `Sample*` prefix) and archetypes are declared exactly once. Discovery off the classpath is automatic (`ClasspathNotationMedia`).
  - **Dist ships the samples' notation on the classpath, not loose.** The `dist` task copies only `notation/main/` as loose disk seed; the `auto-*` sample docs are read-only and travel inside `main.jar`. Widening that copy to all of `notation/` would double-serve them as writable disk documents.
  - **The JS sample needs the kotlin-wrappers on kzen-project-js's compile classpath** (react / reactDom / emotion.styled, added to `kzen-project-js/build.gradle.kts` + the catalog in `settings.gradle.kts`). kzen-auto-js declares them `implementation`, so they don't leak transitively — a downstream client-extension author adds them the same way.
  - This is the **static** extension story (compile-time KSP + explicit `register()`). A **dynamic** plugin-JAR `ModuleReflection` registration cousin is a *pending decision* (EXT D1 / reflection-plan R5) — not an available mechanism yet.
- **Dist zip is a Gradle task.** `:kzen-project-jvm:dist` produces `build/dist/kzen-project-<v>.zip` — thin `main.jar` (`Class-Path` → `dependencies/`) + `dependencies/` + the loose seed notation under `src/main/resources/notation/main/`. Not wired into `build`, so run it explicitly to make a new dist for kzen-shell to consume.
- **Cross-sibling version pin.** `buildSrc/.../Dependencies.kt` pins `kzenAutoVersion` to the kzen-auto source version. Variant-suffix coords route through mavenLocal regardless of the composite, so this must match what kzen-auto has published.

## Pointers

- **Foundational concepts** → [`../kzen-lib/docs/architecture.md`](../kzen-lib/docs/architecture.md).
- **Parent framework** → [`../kzen-auto/AGENTS.md`](../kzen-auto/AGENTS.md) (and [`../kzen-auto/docs/architecture.md`](../kzen-auto/docs/architecture.md) for paradigms, graph sync, report execution).
- **Composite build + toolchain** → [`../kzen/AGENTS.md`](../kzen/AGENTS.md).
- **Runtime host** → [`../kzen-shell/AGENTS.md`](../kzen-shell/AGENTS.md) (the desktop entry point that spawns this project).
