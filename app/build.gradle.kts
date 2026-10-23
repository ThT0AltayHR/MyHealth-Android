plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Her CI derlemesi bir oncekinin ustune guncelleme olarak kurulabilsin diye
// versionCode dakika cinsinden zamandan uretilir (depo yeniden kurulsa bile artar).
val buildMinutes = (System.currentTimeMillis() / 60_000L).toInt()
val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0

fun envOrNull(name: String): String? = System.getenv(name)?.takeIf { it.isNotBlank() }

android {
    namespace = "com.myhealth.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.myhealth.app"
        minSdk = 26
        targetSdk = 36
        versionCode = buildMinutes
        versionName = "1.0.$runNumber"
    }

    val releaseKeystore = rootProject.file("release.keystore")
    signingConfigs {
        create("release") {
            if (releaseKeystore.exists()) {
                val storePass = envOrNull("KEYSTORE_PASSWORD") ?: "myhealth-ci"
                storeFile = releaseKeystore
                storePassword = storePass
                keyAlias = envOrNull("KEY_ALIAS") ?: "myhealth"
                keyPassword = envOrNull("KEY_PASSWORD") ?: storePass
            }
        }
    }

    buildTypes {
        release {
            // Kararlilik oncelikli: kod kuculme (R8) kapali, boylece yansima/medya/WebView
            // kaynakli calisma zamani cokmeleri olusamaz.
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = if (releaseKeystore.exists()) signingConfigs.getByName("release")
            else signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    androidResources { noCompress += listOf("mp4", "html", "js") }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    lint { abortOnError = false }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-session:1.4.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // Yalnizca aktivite algilama (yuruyus/kosu/bisiklet) icin; GMS yoksa kod sessizce devre disi kalir.
    implementation("com.google.android.gms:play-services-location:21.3.0")
}
