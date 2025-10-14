import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform") version "1.9.20"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.vanniktech.maven.publish") version "0.34.0"
    signing
}

val PUBLISH_GROUP_ID = "com.github.adriankuta"
val PUBLISH_ARTIFACT_ID = "tree-structure"    // base artifact; KMP will add -jvm, -ios*, etc.
val PUBLISH_VERSION = "3.1.1"

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
        description.set("Simple implementation to store object in tree structure.")
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

// No legacy publishing {} block or s01 repos — Central Portal handles it.

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
kotlin {
    jvmToolchain(21);
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"   // <- was "1.8"
            }
        }
    }

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Native host target
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting { dependencies { implementation(kotlin("test")) } }
        val jvmMain by getting { dependencies { implementation(kotlin("script-runtime")) } }
        val jvmTest by getting
        val nativeMain by getting
        val nativeTest by getting

        // Shared iOS source sets
        val iosMain by creating { dependsOn(commonMain) }
        val iosTest by creating { dependsOn(commonTest) }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }
    }
}
