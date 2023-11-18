plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("realm-android")
}

android {
    compileSdkVersion(33)

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.tstudioz.fax.fme"
        minSdkVersion(21)
        targetSdkVersion(33)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        isEnabled = true
    }
}

dependencies {

    val koin_version = "2.1.4"
    implementation("org.koin:koin-android:$koin_version")
    implementation("org.koin:koin-android-scope:$koin_version")
    implementation("org.koin:koin-android-viewmodel:$koin_version")
    implementation("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("io.coil-kt:coil:0.9.5")
    implementation("nl.joery.animatedbottombar:library:1.0.7")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation("com.philliphsu:bottomsheetpickers:2.4.1")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference:1.1.0")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.browser:browser:1.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.4.1")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("io.realm:android-adapters:2.1.1")
    implementation("com.github.doctoror.particlesdrawable:library:1.0.9")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.lizhangqu:coreprogress:1.0.2")
    implementation("com.mikhaellopez:circularprogressbar:3.0.2")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    implementation("com.orhanobut:hawk:2.0.1")
    testImplementation("junit:junit:4.12")
    implementation("androidx.appcompat:appcompat:1.6.1")

    val multidex_version = "2.0.1"
    implementation("androidx.multidex:multidex:$multidex_version")

    val lifecycle_version = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
    }
}