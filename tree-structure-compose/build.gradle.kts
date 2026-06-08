import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    signing
}

group = "com.github.adriankuta"
version = rootProject.version

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates("com.github.adriankuta", "tree-structure-compose", version.toString())

    pom {
        name.set("Tree Data Structure — Compose Multiplatform")
        description.set("A LazyTree composable (expand/collapse, lazy rendering) for the tree-structure library.")
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

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            // Resolve this module's GitHub source path relative to the repo root.
            localDirectory.set(projectDir.resolve("src"))
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    jvm()

    androidTarget {
        publishLibraryVariants("release")
        // Match the Android variant's Kotlin bytecode to the Java level set in compileOptions.
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":"))
            implementation(compose.runtime)
            implementation(compose.foundation)
        }
    }
}

android {
    namespace = "com.github.adriankuta.datastructure.tree.compose"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
