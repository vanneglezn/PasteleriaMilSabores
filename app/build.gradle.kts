plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    kotlin("kapt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.pasteleriamilsabores"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pasteleriamilsabores"
        minSdk = 26
        targetSdk = 34
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
    }

    // Debe alinear con tu BOM/compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }
}

dependencies {
    // BOM de Compose
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // UI Compose (desde BOM)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Animaciones nativas (versionada por el BOM)
    implementation("androidx.compose.animation:animation")

    // Navigation Compose (usa el alias del catálogo)
    implementation(libs.androidx.navigation.compose)

    // Íconos extendidos (desde BOM)
    implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation("com.google.maps.android:maps-compose:4.4.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    val mockitoVersion = "5.11.0" // Versión estable
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")





    val roomVersion = "2.6.1"
    val retrofitVersion = "2.9.0"

    // --- Local Storage: Room (Persistencia de pedidos) ---
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // --- APIs Externas: Retrofit/Gson (Para consumir el Gist/otras APIs) ---
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // --- APIs Externas: Google Maps (Para la ubicación del local) ---
    implementation("com.google.maps.android:maps-compose:4.4.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")


    // Herramientas de depuración
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Tests
    testImplementation(libs.junit)


    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}