plugins {
    // No version: the Kotlin Gradle plugin is already on the build classpath via the root
    // project's kotlinMultiplatform plugin, so requesting a version here would clash.
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.adriankuta.samples.SamplesKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation(project(":tree-structure-serialization"))
    implementation(project(":tree-structure-coroutines"))
    implementation(project(":tree-structure-immutable"))
    // ImmutableTreeNode.children returns a PersistentList, so consumers that touch it need
    // kotlinx.collections.immutable on their own classpath (the module declares it as implementation).
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(kotlin("test"))
}

// kotlin("test") auto-selects the JUnit 5 adapter when the test task uses the JUnit Platform.
tasks.test {
    useJUnitPlatform()
}
