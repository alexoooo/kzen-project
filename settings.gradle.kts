rootProject.name = "kzen-project"

include("kzen-project-common", "kzen-project-js", "kzen-project-jvm")


// kotlin-wrappers catalog, so kzen-project-js can define its own React client extensions (e.g. the
//  sample AttributeView). Mirror kzen-auto / kzen-launcher: same wrappers version across all JS siblings
//  (umbrella toolchain rule). The catalog artifact resolves from mavenCentral.
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("kotlinWrappers") {
            val wrappersVersion = "2026.7.1"
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
    }
}
