@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
//    `maven-publish`
}


kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmToolchainVersion))
    }
}


dependencies {
    implementation(project(":kzen-project-common"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("tech.kzen.auto:kzen-auto-common-jvm:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-jvm:$kzenAutoVersion")
}


tasks.withType<ProcessResources> {
    val jsProject = project(":kzen-project-js")

    val browserDistributionTask = jsProject.tasks.getByName("jsBrowserDistribution")
    dependsOn(browserDistributionTask)

    val task = jsProject.tasks.getByName("jsBrowserProductionWebpack") as KotlinWebpack
    dependsOn(task)

    from(task.outputDirectory) {
        into("static")
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xjsr305=strict")
        jvmTarget = jvmTargetVersion
    }
}


tasks.compileJava {
    options.release.set(javaVersion)
}


val dependenciesDir = "dependencies"
task("copyDependencies", Copy::class) {
    from(configurations.runtimeClasspath)
        .into("${layout.buildDirectory.get().asFile}/libs/$dependenciesDir")
}


tasks.getByName<Jar>("jar") {
    val jvmProject = project(":kzen-project-jvm")
    val copyDependenciesTask = jvmProject.tasks.getByName("copyDependencies") as Copy
    dependsOn(copyDependenciesTask)

    manifest {
        attributes["Main-Class"] = "tech.kzen.project.server.KzenProjectMainKt"
        attributes["Class-Path"] = configurations
            .runtimeClasspath
            .get()
            .joinToString(separator = " ") { file ->
                "$dependenciesDir/${file.name}"
            }
    }
}