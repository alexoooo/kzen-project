# kzen-project — AI agent guide

## Purpose

kzen-project is the **office-automation product** built on top of kzen-auto. It's a thin shell that boots kzen-auto's server with a project-specific module registration. Most of the heavy lifting (UI, paradigms, plugin SPI) lives in `../kzen-auto`; this repo registers project-specific objects and packages the result as a distributable.

Read [`../kzen-lib/docs/architecture.md`](../kzen-lib/docs/architecture.md) and [`../kzen-auto/AGENTS.md`](../kzen-auto/AGENTS.md) before working here — kzen-project assumes you understand both.

## Module layout

Three Gradle subprojects (KMP shape, no plugin module):

- **`kzen-project-common`** — KMP shared code (`commonMain`/`commonTest`). Project-specific notation objects and shared models. Currently lean (the bulk of common logic is in kzen-auto-common).
- **`kzen-project-jvm`** — JVM server. Entry point `tech.kzen.project.server.KzenProjectMain` (`fun main`) which delegates to `kzenAutoInit` + `kzenAutoMain` from kzen-auto-jvm.
- **`kzen-project-js`** — Kotlin/JS frontend. Entry point `tech.kzen.project.client.Main`.

## Entry points

| Class | Module | Purpose |
|----|----|----|
| `tech.kzen.project.server.KzenProjectMain` | kzen-project-jvm | Production server `fun main`. Registers `KzenProjectCommonModule` + `KzenProjectJvmModule`, then calls `kzenAutoInit` + `kzenAutoMain`. |
| `tech.kzen.project.client.Main` | kzen-project-js | JS entry point (bundled by webpack). |

> The README mentions `KzenProjectApp` — that name doesn't exist in source. The actual class is `KzenProjectMain` (`KzenProjectMain.kt:14`). Use `KzenProjectMain` in run configs.

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

The fat jar is what kzen-shell ultimately spawns at runtime (after being repackaged into `kzen-project-<v>.zip` — that zip is hand-built from `kzen-project-jvm/build/libs/` outputs, NOT produced by any Gradle task).

## Key directories

| Path | What lives here |
|----|----|
| `kzen-project-common/src/commonMain/kotlin/tech/kzen/project/common/codegen/KzenProjectCommonModule.kt` | Common-side module registration (notation objects, defs/creators) |
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/server/KzenProjectMain.kt` | JVM entry point |
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/server/codegen/KzenProjectJvmModule.kt` | JVM-side module registration |
| `kzen-project-jvm/src/main/kotlin/tech/kzen/project/common/CommonServer.kt` | Server-side common helpers |
| `kzen-project-js/src/jsMain/kotlin/tech/kzen/project/client/Main.kt` | JS entry point |
| `kzen-project-js/src/jsMain/kotlin/tech/kzen/project/client/codegen/KzenProjectJsModule.kt` | JS-side module registration |

## Gotchas

- **kzen-auto plugin publish dependency.** kzen-project depends on `kzen-auto-plugin` through the normal mavenLocal route (variant-suffix coords). After any change to `kzen-auto-plugin`, run `cd ../kzen-auto && ./gradlew :kzen-auto-plugin:publishToMavenLocal` before building kzen-project standalone.
- **README class name is wrong.** Use `KzenProjectMain`, not `KzenProjectApp`. The umbrella AGENTS.md also propagates the stale name — fix it there if you're updating both.
- **Dist zip is hand-built.** No Gradle task produces `kzen-project-<v>.zip`. To make a new dist for kzen-shell to consume, rebuild, rename the fat jar to `main.jar`, bundle with `dependencies/`, and zip.
- **Cross-sibling version pin.** `buildSrc/.../Dependencies.kt` pins `kzenAutoVersion = "0.29.1-SNAPSHOT"`. Variant-suffix coords route through mavenLocal regardless of the composite, so this must match what kzen-auto has published.

## Pointers

- **Foundational concepts** → [`../kzen-lib/docs/architecture.md`](../kzen-lib/docs/architecture.md).
- **Parent framework** → [`../kzen-auto/AGENTS.md`](../kzen-auto/AGENTS.md) (and [`../kzen-auto/docs/architecture.md`](../kzen-auto/docs/architecture.md) for paradigms, graph sync, report execution).
- **Composite build + toolchain** → [`../kzen/AGENTS.md`](../kzen/AGENTS.md).
- **Runtime host** → [`../kzen-shell/AGENTS.md`](../kzen-shell/AGENTS.md) (the desktop entry point that spawns this project).
