import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform") version "1.7.20"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    signing
}

val PUBLISH_GROUP_ID = "com.github.adriankuta"
val PUBLISH_ARTIFACT_ID = "tree-structure"
val PUBLISH_VERSION = "3.0.1"

val secretFile = File(rootProject.rootDir, "local.properties")
if (secretFile.exists()) {
    secretFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        project.ext[name.toString()] = value
    }
} else {
    project.ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    project.ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    project.ext["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID")
    project.ext["signingKeyId"] = System.getenv("SIGNING_KEY_ID")
    project.ext["signingPassword"] = System.getenv("SIGNING_PASSWORD")
    project.ext["signingKey"] = System.getenv("SIGNING_KEY")
    project.ext["snapshot"] = System.getenv("SNAPSHOT")
}
val snapshot: String? by project

group = PUBLISH_GROUP_ID
version = if (snapshot.toBoolean()) "$PUBLISH_VERSION-SNAPSHOT" else PUBLISH_VERSION

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

publishing {
    publications {

        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("Tree Data Structure")
                description.set("Simple implementation to store object in tree structure.")
                url.set("https://github.com/AdrianKuta/Tree-Data-Structure")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.mit.edu/~amini/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        name.set("Adrian Kuta")
                        email.set("adrian.kuta93@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/AdrianKuta/Tree-Data-Structure.git")
                    developerConnection.set("scm:git:ssh://github.com/AdrianKuta/Tree-Data-Structure.git")
                    url.set("https://github.com/AdrianKuta/Tree-Data-Structure/tree/master")
                }
            }
        }
    }

    repositories {
        maven {
            name = "Sonatype"
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingPassword: String? by project
    val signingKey: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)

    sign(publishing.publications)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
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
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("script-runtime"))
            }
        }
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}
