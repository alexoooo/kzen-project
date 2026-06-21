import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}


kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(jvmToolchainVersion))
    }


    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion))
        }
//        @Suppress("UNUSED_VARIABLE")
//        val main by compilations.getting {
//            kotlinOptions {
//                jvmTarget = jvmTargetVersion
//            }
//        }
    }


    js {
        browser {
            testTask {
                testLogging {
                    showExceptions = true
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    showCauses = true
                    showStackTraces = true
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
//                implementation("tech.kzen.lib:kzen-lib-common:$kzenLibVersion")
            implementation("tech.kzen.auto:kzen-auto-common:$kzenAutoVersion")
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }


        jvmMain.dependencies {
            implementation("tech.kzen.auto:kzen-auto-common-jvm:$kzenAutoVersion")
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
        }


        jsMain.dependencies {
            implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
        }
    }
}


dependencies {
    add("kspCommonMainMetadata", "tech.kzen.lib:kzen-lib-reflect-ksp:$kzenLibVersion")
}


ksp {
    arg("kzen.reflect.moduleClassName", "tech.kzen.project.common.codegen.KzenProjectCommonModule")
}


// KSP commonMain output isn't picked up by per-target compile tasks automatically — same wiring as
// kzen-lib-common / kzen-auto-common.
kotlin.sourceSets.commonMain.configure {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
tasks.matching { it.name == "sourcesJar" || it.name.endsWith("SourcesJar") }
    .configureEach { dependsOn("kspCommonMainKotlinMetadata") }
