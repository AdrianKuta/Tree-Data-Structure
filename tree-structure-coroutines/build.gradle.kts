import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    signing
}

group = "com.github.adriankuta"
version = rootProject.version

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates("com.github.adriankuta", "tree-structure-coroutines", version.toString())

    pom {
        name.set("Tree Data Structure — coroutines")
        description.set("kotlinx.coroutines Flow traversal for the tree-structure library.")
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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":"))
            api(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
