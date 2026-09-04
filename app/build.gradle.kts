plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mineinspect.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mineinspect.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Placeholder backend URL — the FastAPI backend has not been built yet (see
        // INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md). Point this at the real deployment
        // once it exists; for local development against an emulator running a backend
        // on the host machine, use "http://10.0.2.2:8000/api/v1/".
        buildConfigField("String", "API_BASE_URL", "\"https://api.mineinspect.example.com/api/v1/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Points at `uvicorn app.main:app --host 0.0.0.0 --port 8000` from
            // mineguard-backend, running on the dev PC. Tunneled over the USB cable via
            // `adb reverse tcp:8000 tcp:8000` rather than a shared-WiFi LAN IP, since
            // campus WiFi blocks device-to-device traffic (confirmed: phone could not
            // even ping the PC despite being on the same subnet). Run that adb command
            // once per USB connection/reconnection before testing.
            // If testing on the Android *emulator* instead, use
            // "http://10.0.2.2:8000/api/v1/" (its alias for the host's localhost).
            buildConfigField("String", "API_BASE_URL", "\"http://127.0.0.1:8000/api/v1/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ViewModel + Compose state collection
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Hilt (DI) — see INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md §4/§20 Phase 1
    // Uses KSP (not kapt) for annotation processing: Kotlin 2.1.20 + kapt fails to read
    // this project's own Kotlin metadata during Hilt's stub generation ("unsupported
    // metadata kind"), a known kapt/K2 incompatibility. Hilt 2.56+ and hilt-work 1.2.0
    // both support KSP, so this sidesteps the bug entirely instead of working around it.
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Room (offline-first local database) — plan §5
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // WorkManager (background sync) — plan §10
    // Pinned to 2.9.1: 2.10.0+ requires compileSdk 35 + AGP 8.6+, which this project
    // doesn't use yet (compileSdk 34 / AGP 8.3.2) — bumping those is out of scope for
    // Phase 1 foundation work.
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Networking — plan §12/§13 (FastAPI REST contract)
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Encrypted token storage — plan §11/§18
    implementation("androidx.security:security-crypto:1.1.0")

    // GPS (real fixes — replaces GpsGateScreen's fabricated GNSS data) — plan §7
    implementation("com.google.android.gms:play-services-location:21.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
