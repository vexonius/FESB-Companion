plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("io.realm.kotlin") version "1.16.0"
}

android {
    compileSdk = 34
    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.tstudioz.fax.fme"
        minSdk = 26
        targetSdk = 34
        versionCode = 21
        versionName = "2.3.0 build #2307"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    namespace = "com.tstudioz.fax.fme"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xdebug") // da ne bi bilo optimised out vars
    }

    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        compose=true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
}

dependencies {

    val koinVersion = "3.5.6"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.github.doctoror.particlesdrawable:library:2.0.2")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.lizhangqu:coreprogress:1.0.2")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    implementation("com.orhanobut:hawk:2.0.1")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.9.0")

    val multidexVersion = "2.0.1"
    implementation("androidx.multidex:multidex:$multidexVersion")

    val lifecycleVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("io.realm.kotlin:library-base:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material3:material3-android:1.2.1")

    val composeVersion = "1.6.7"

    //compose livedata state
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //EncryptedSharedPreferences
    implementation("androidx.security:security-crypto:1.0.0")

    //pull to refresh compose
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-android:$composeVersion")

    //choose calendar
    implementation("com.kizitonwose.calendar:compose:2.5.0")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}