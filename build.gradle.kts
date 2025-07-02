// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        mavenCentral()
        maven("https://maven.google.com/")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
        google()
    }
    dependencies {
        classpath(libs.gradle)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin}")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
        google()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

plugins {
    // Existing plugins
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
}