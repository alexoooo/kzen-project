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

    implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}


run {}