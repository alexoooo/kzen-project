plugins {
    id("org.jetbrains.kotlin.js")
}


kotlin {
    js {
        useCommonJs()

//        produceExecutable()

        browser {
            webpackTask {
                // TODO: hot-reload breaks?
//                outputFileName = "index.js"
            }
        }
    }
}


dependencies {
    implementation(project(":kzen-project-common"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")

//    implementation("tech.kzen.lib:kzen-lib-common-js:$kzenLibVersion")
//    implementation("tech.kzen.lib:kzen-lib-js:$kzenLibVersion")
    implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")
}


run {}