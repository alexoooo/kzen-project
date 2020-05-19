plugins {
    id("org.jetbrains.kotlin.js")
}


kotlin {
    target {
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")

    implementation(project(":kzen-project-common"))

    implementation(npm("core-js", coreJsVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-html-assembly:$kotlinxHtmlVersion")
    implementation("org.jetbrains:kotlin-react:$kotlinxReactVersion")
    implementation("org.jetbrains:kotlin-react-dom:$kotlinxReactDomVersion")
    implementation("org.jetbrains:kotlin-styled:$kotlinxStyledVersion")
    implementation("org.jetbrains:kotlin-extensions:$kotlinxExtensionsVersion")
    implementation("org.jetbrains:kotlin-css-js:$kotlinxCssVersion")
    implementation(npm("react", reactVersion))
    implementation(npm("react-dom", reactVersion))
    implementation(npm("react-is", reactVersion))
    implementation(npm("inline-style-prefixer", inlineStylePrefixerVersion))
    implementation(npm("styled-components", styledComponentsVersion))
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")

    implementation("tech.kzen.lib:kzen-lib-common-js:$kzenLibVersion")
    implementation("tech.kzen.lib:kzen-lib-js:$kzenLibVersion")
    implementation("tech.kzen.auto:kzen-auto-common-js:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-js:$kzenAutoVersion")

    implementation(npm("@material-ui/core", materialUiCoreVersion))
    implementation(npm("@material-ui/icons", materialUiIconsVersion))
    implementation(npm("cropperjs", cropperJsVersion))
    implementation(npm("lodash", lodashVersion))
    implementation(npm("react-select", reactSelectVersion))
}


run {}