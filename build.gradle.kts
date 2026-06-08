import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.kover)
    signing
    alias(libs.plugins.androidLibrary)
    // Loaded once here (with a known version) so the Android application/library variants can be
    // applied across subprojects without the "already on the classpath with an unknown version" clash.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}

val PUBLISH_GROUP_ID = "com.github.adriankuta"
val PUBLISH_ARTIFACT_ID = "tree-structure"    // base artifact; KMP will add -jvm, -ios*, etc.
val PUBLISH_VERSION = "4.1.1"

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
    google()
}

dependencies {
    // Include this module's own docs in the aggregation — DGP v2 requires the
    // aggregating project to list itself explicitly.
    dokka(project(":"))
    dokka(project(":tree-structure-serialization"))
    dokka(project(":tree-structure-coroutines"))
    dokka(project(":tree-structure-compose"))
    dokka(project(":tree-structure-immutable"))
}

dokka {
    moduleName.set("Tree Data Structure")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            // For the root project projectDir == rootDir, so `module` is "" and links resolve to /blob/master/src.
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}

apiValidation {
    // The sample app is not a published library — exclude it from binary-compatibility validation.
    ignoredProjects.add("samples")
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    jvm()

    androidTarget {
        publishLibraryVariants("release")
        // Build the Android variant at JVM 17 so Android consumers (JVM 11/17) can inline the
        // library's inline DSL (`tree { }`) — they cannot inline the default JVM-21 bytecode.
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

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

android {
    namespace = "com.github.adriankuta.datastructure.tree"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
