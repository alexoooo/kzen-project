import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn


plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}


// Read via providers.gradleProperty (tracked by the configuration cache), NOT properties.containsKey
// (reads the untracked legacy project-properties map). With containsKey, a cached config entry built
// without -PjsWatch is silently reused on later -PjsWatch runs. kzen-project's dev loop uses
// webpack-dev-server, but keep the read consistent so the production esbuild bundle is never built in
// dev mode by a stale cache entry.
val devMode = providers.gradleProperty("jsWatch").isPresent


kotlin {
    js {
        useCommonJs()
        binaries.executable()

        browser {
            val webpackMode =
                if (devMode) {
                    KotlinWebpackConfig.Mode.DEVELOPMENT
                }
                else {
                    KotlinWebpackConfig.Mode.PRODUCTION
                }

            commonWebpackConfig {
                mode = webpackMode
            }
        }

        if (devMode) {
            compilations.all {
                compileTaskProvider.configure {
                    compilerOptions.freeCompilerArgs.add("-Xir-minimized-member-names=false")
                }
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":kzen-project-common"))

            implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
            implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")

            // esbuild bundler (replaces webpack for the production bundle) — see jsEsbuildBundle below
            implementation(npm("esbuild", esbuildVersion))
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

run {}


dependencies {
    add("kspJs", "tech.kzen.lib:kzen-lib-reflect-ksp:$kzenLibVersion")
}


ksp {
    arg("kzen.reflect.moduleClassName", "tech.kzen.project.client.codegen.KzenProjectJsModule")
}


// https://youtrack.jetbrains.com/issue/KT-52578/KJS-Gradle-KotlinNpmInstallTask-gradle-task-produces-unsolvable-warning-ignored-scripts-due-to-flag.
yarn.ignoreScripts = false


// === esbuild bundler (replaces webpack for the production bundle) ================================
// kzen-project re-bundles kzen-auto-js, which now deep-imports only referenced MUI icons (no more
// require.context), so esbuild can bundle it. The production jar's static bundle is produced by
// esbuild; the dev inner loop still uses webpack-dev-server (`:kzen-project-js:run`), so only the
// production webpack tasks are disabled below.

val npmPackageName = "${rootProject.name}-${project.name}"
// The compileSync output dir holds one .js per Gradle module (kzen-auto-kzen-auto-js.js, kzen-lib-*.js,
// stdlib, …); the entry only require()s them. Declare the whole dir as the task input — NOT just the
// entry file — so a change in any dependency module (kzen-auto-js, kzen-lib) re-triggers the bundle.
// With only inputs.file(entry), such a change lands in a sibling file, the entry stays byte-identical,
// and jsEsbuildBundle wrongly stays UP-TO-DATE.
val esbuildInputDir = rootProject.layout.buildDirectory
    .dir("js/packages/$npmPackageName/kotlin")
val esbuildEntry = esbuildInputDir.map { it.file("$npmPackageName.js") }
val esbuildOutFile = layout.buildDirectory
    .file("dist/js/productionExecutable/${project.name}.js")

fun esbuildBinaryPath(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    val isArm = arch.contains("aarch64") || arch.contains("arm")
    val (pkg, exe) = when {
        os.contains("win") -> "win32-x64" to "esbuild.exe"
        os.contains("mac") || os.contains("darwin") ->
            (if (isArm) "darwin-arm64" else "darwin-x64") to "bin/esbuild"
        else -> (if (isArm) "linux-arm64" else "linux-x64") to "bin/esbuild"
    }
    return rootProject.layout.buildDirectory
        .file("js/node_modules/@esbuild/$pkg/$exe").get().asFile.absolutePath
}

tasks.register<Exec>("jsEsbuildBundle") {
    group = "kotlin browser"
    description = "Bundle the Kotlin/JS output with esbuild (replaces the production webpack bundle)"

    dependsOn("jsProductionExecutableCompileSync")
    // esbuild resolves react / react-dom / etc. from build/js/node_modules; kotlinNpmInstall populates
    // it. The compileSync tasks don't depend on it (the compiler only emits require() calls, it doesn't
    // need the modules present), so without this edge esbuild can run against an empty node_modules and
    // fail with "Could not resolve react". webpack's bundle task depended on kotlinNpmInstall for this.
    dependsOn(rootProject.tasks.named("kotlinNpmInstall"))

    inputs.dir(esbuildInputDir)
    outputs.file(esbuildOutFile)

    val invocation = buildList {
        add(esbuildBinaryPath())
        add(esbuildEntry.get().asFile.absolutePath)
        add("--bundle")
        add("--format=iife")
        add("--platform=browser")
        add("--sourcemap")
        add("--outfile=${esbuildOutFile.get().asFile.absolutePath}")
        add("--minify")
        add("--legal-comments=external")
    }
    commandLine(invocation)
}

// Production bundle is produced by esbuild; disable the production webpack tasks. The development
// webpack tasks stay enabled for the webpack-dev-server dev loop (`:kzen-project-js:run`).
tasks.matching {
    it.name == "jsBrowserProductionWebpack" || it.name == "jsBrowserDistribution"
}.configureEach {
    enabled = false
}