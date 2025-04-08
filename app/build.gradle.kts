plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.humber.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.humber.weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material.v1130alpha11)
    implementation(libs.google.firebase.auth)
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.android.gms:play-services-base")
    implementation ("com.google.firebase:firebase-bom:33.11.0")
    // JUnit dependencies for unit testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")

// Mocking library for Kotlin (MockK)
    testImplementation("io.mockk:mockk:1.12.0")

// AndroidX Test dependencies for UI testing
    testImplementation("androidx.test.ext:junit:1.1.3")  // For running tests with JUnit
    testImplementation("androidx.test:core:1.4.0")      // Core test libraries for Android

// Espresso for UI testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")   // Basic UI testing with Espresso
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0") // For handling async tasks during UI testing

// Additional test dependencies for mocking Firebase or other external resources (if needed)
    androidTestImplementation("com.google.firebase:firebase-auth-ktx:21.0.1") // Firebase authentication for mocking
    androidTestImplementation("com.google.firebase:firebase-firestore-ktx:24.0.0") // Firebase Firestore for mocking

// For testing Android Lifecycle components (optional but useful for lifecycle-dependent tests)
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")  // For testing LiveData, ViewModels, etc.

// For Kotlin Coroutines (useful if your code involves suspending functions or coroutines)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")  // Coroutines for UI thread testing
    testImplementation(kotlin("test"))


}