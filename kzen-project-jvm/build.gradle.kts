import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack


plugins {
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyManagementVersion
    kotlin("jvm")
    kotlin("plugin.spring") version kotlinVersion
    id("com.github.johnrengelman.shadow") version shadowVersion
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
    val task = jsProject.tasks.getByName("browserProductionWebpack") as KotlinWebpack

    from(task.destinationDirectory) {
        into("public")
    }

    dependsOn(task)
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = jvmTargetVersion
    }
}


tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("kzen-project")
    isZip64 = true
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "tech.kzen.project.server.KzenProjectMainKt"))
    }
}
