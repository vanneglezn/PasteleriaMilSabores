plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.pasteleriamilsabores"

    // Evita el warning de "Unsupported API" mientras tanto
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

    // Asegura que el compiler de Compose coincide con el BOM
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }
}

dependencies {
    // BOM de Compose: controla las versiones de todos los artefactos Compose
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // UI Compose (tomará versión desde el BOM)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Necesario para fadeIn/fadeOut y demás transiciones nativas
    implementation("androidx.compose.animation:animation")

    // Navigation Compose (DEJA SOLO UNO; usamos el alias)
    implementation(libs.androidx.navigation.compose)
    // implementation("androidx.navigation:navigation-compose:2.8.0") // <- no usar si ya usas el alias

    // Íconos extendidos (desde BOM)
    implementation("androidx.compose.material:material-icons-extended")

    // StateFlow + Compose (alineados en 2.8.7)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Herramientas de depuración
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
