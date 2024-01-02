plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("realm-android")
}

android {
    compileSdk = 34
    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.tstudioz.fax.fme"
        minSdk = 21
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
    }

    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    val koinVersion = "2.1.4"
    implementation("org.koin:koin-android:$koinVersion")
    implementation("org.koin:koin-android-scope:$koinVersion")
    implementation("org.koin:koin-android-viewmodel:$koinVersion")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.philliphsu:bottomsheetpickers:2.4.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.browser:browser:1.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("io.realm:android-adapters:2.1.1")
    implementation("io.realm:realm-gradle-plugin:10.13.3-transformer-api") // added to try to remobe the transform error
    implementation("com.github.doctoror.particlesdrawable:library:2.0.2")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.lizhangqu:coreprogress:1.0.2")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    implementation("com.orhanobut:hawk:2.0.1")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.6.1")


    val multidexVersion = "2.0.1"
    implementation("androidx.multidex:multidex:$multidexVersion")

    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
    }
}