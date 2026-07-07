plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.xc.code.editor.core"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("17"))
        }
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api("androidx.compose.runtime:runtime")
    api(libs.kotlinx.coroutines.core)
    api(project(":modules:sora-editor"))
}
