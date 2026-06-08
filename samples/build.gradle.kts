plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    // Match the Kotlin bytecode target to the Java level in android.compileOptions below.
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.github.adriankuta.treestructure.sample"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.adriankuta.treestructure.sample"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidCompileSdk.get().toInt()
        versionCode = 1
        versionName = rootProject.version.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":"))
    implementation(project(":tree-structure-compose"))

    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.components.uiToolingPreview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(compose.uiTooling)
}
