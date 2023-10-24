import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    kotlin("multiplatform")
}


val devMode = properties.containsKey("jsWatch")


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
        val jsMain by getting {
            dependencies {
                implementation(project(":kzen-project-common"))

                implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
                implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


//dependencies {
//    implementation(project(":kzen-project-common"))
//
//    implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
//    implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")
//
//    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
//}


run {}


// https://youtrack.jetbrains.com/issue/KT-52578/KJS-Gradle-KotlinNpmInstallTask-gradle-task-produces-unsolvable-warning-ignored-scripts-due-to-flag.
yarn.ignoreScripts = false