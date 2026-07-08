@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.register


plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
//    `maven-publish`
}


kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmToolchainVersion))
    }
}


dependencies {
    implementation(project(":kzen-project-common"))

    ksp("tech.kzen.lib:kzen-lib-reflect-ksp:$kzenLibVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("tech.kzen.auto:kzen-auto-common-jvm:$kzenAutoVersion")
    implementation("tech.kzen.auto:kzen-auto-jvm:$kzenAutoVersion")
}


ksp {
    arg("kzen.reflect.moduleClassName", "tech.kzen.project.server.codegen.KzenProjectJvmModule")
}


tasks.withType<ProcessResources> {
    val jsProject = project(":kzen-project-js")

    // esbuild bundle (replaces the production webpack bundle) → build/dist/js/productionExecutable/
    val bundleTask = jsProject.tasks.named("jsEsbuildBundle")
    dependsOn(bundleTask)

    from(jsProject.layout.buildDirectory.dir("dist/js/productionExecutable")) {
        into("static")
    }
}


//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs += listOf("-Xjsr305=strict")
//        jvmTarget = jvmTargetVersion
//    }
//}


tasks.compileJava {
    options.release.set(javaVersion)
}


val dependenciesDir = "dependencies"
tasks.register<Copy>("copyDependencies") {
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


// Distribution zip: main.jar (the thin jar, Class-Path -> dependencies/) + dependencies/ at the
//  root — the archetype layout the launcher's ProjectCreator unzips verbatim into a new project.
tasks.register<Zip>("dist") {
    dependsOn("jar", "copyDependencies")
    archiveFileName.set("kzen-project-$version.zip")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))

    from(tasks.named("jar")) { rename { "main.jar" } }
    from(layout.buildDirectory.dir("libs/$dependenciesDir")) { into(dependenciesDir) }
}