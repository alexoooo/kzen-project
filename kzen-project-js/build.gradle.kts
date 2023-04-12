import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.js")
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
}


dependencies {
    implementation(project(":kzen-project-common"))

    implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}


run {}