import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    kotlin("multiplatform")
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
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
//                implementation("tech.kzen.lib:kzen-lib-common:$kzenLibVersion")
                implementation("tech.kzen.auto:kzen-auto-common:$kzenAutoVersion")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }


        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting {
            dependencies {
                implementation("tech.kzen.auto:kzen-auto-common-jvm:$kzenAutoVersion")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }


        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            dependencies {
                implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
