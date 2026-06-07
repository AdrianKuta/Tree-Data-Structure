import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.kover)
    signing
}

val PUBLISH_GROUP_ID = "com.github.adriankuta"
val PUBLISH_ARTIFACT_ID = "tree-structure"    // base artifact; KMP will add -jvm, -ios*, etc.
val PUBLISH_VERSION = "4.0.0"

val snapshot: String? by project

group = PUBLISH_GROUP_ID
version = if (snapshot.toBoolean()) "$PUBLISH_VERSION-SNAPSHOT" else PUBLISH_VERSION

mavenPublishing {
    // Central Portal + auto release when we call publishAndReleaseToMavenCentral
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates(PUBLISH_GROUP_ID, PUBLISH_ARTIFACT_ID, version.toString())

    pom {
        name.set("Tree Data Structure")
        description.set(
            "Lightweight n-ary tree data structure for Kotlin Multiplatform (JVM, JS, Wasm, iOS, " +
                "Native). DSL, pre/post/level-order traversal, lazy Sequence traversal, and pretty-print.",
        )
        url.set("https://github.com/AdrianKuta/Tree-Data-Structure")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("AdrianKuta")
                name.set("Adrian Kuta")
                email.set("adrian.kuta93@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/AdrianKuta/Tree-Data-Structure")
            connection.set("scm:git:https://github.com/AdrianKuta/Tree-Data-Structure.git")
            developerConnection.set("scm:git:ssh://git@github.com/AdrianKuta/Tree-Data-Structure.git")
        }
    }
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    // Apple targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Native host target
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
