import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
//import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack


plugins {
//    id("org.springframework.boot") version springBootVersion
//    id("io.spring.dependency-management") version dependencyManagementVersion
    kotlin("jvm")
//    kotlin("plugin.spring") version kotlinVersion
//    id("com.github.johnrengelman.shadow") version shadowVersion
}


kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(jvmToolchainVersion))
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
    val task = jsProject.tasks.getByName("browserProductionWebpack") as org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

    from(task.destinationDirectory) {
        into("static")
    }

    dependsOn(task)
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
    from(configurations.default).into("$buildDir/libs/$dependenciesDir")
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