plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safeArgs)
    }

    repositories {
        google()
        mavenCentral()
    }
}