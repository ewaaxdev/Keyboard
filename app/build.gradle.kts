plugins {
    id("com.android.application")
    kotlin("android")
}

// Blok android: semua pengaturan khusus Android untuk modul app.
android {
    // ID unik aplikasi. Harus sama dengan package name yang dipilih.
    namespace = "com.ewaax.keyboard"
    // API dipakai untuk compile. 36 = Android 16, stabil terbaru.
    compileSdk = 36

    defaultConfig {
        // ID yang dilihat Play Store dan sistem Android.
        applicationId = "com.ewaax.keyboard"
        // Android tertua yang bisa install: API 24 (Android 7.0).
        minSdk = 24
        // API yang perilakunya kita ikuti/test: 36.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            // Tahap 1: matikan minify agar build simpel dan mudah debug.
            isMinifyEnabled = false
        }
    }
    // Kode Java/Kotlin dikompilasi sebagai Java 17 (sesuai JDK di CI).
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

// Sengaja kosong: project ini nol dependency pihak ketiga.
dependencies {
}
