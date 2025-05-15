plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.optlab.nimbus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.optlab.nimbus"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.optlab.nimbus.di.CustomTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "ROOM_DB_DEBUG", "false")
        }

        debug {
            buildConfigField("boolean", "ROOM_DB_DEBUG", "true")
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
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        buildConfig = true
    }
}

configurations.all {
    exclude(group = "org.hamcrest", module = "hamcrest-core")
    exclude(group = "org.hamcrest", module = "hamcrest-library")
    exclude(group = "org.hamcrest", module = "hamcrest")
}

dependencies {
    // --- Core AndroidX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Compose UI ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.testing)
    implementation(libs.androidx.preference)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // --- Navigation ---
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // --- Lombok (boilerplate reduction) ---
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // --- Room Database ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rxjava3)
    annotationProcessor(libs.androidx.room.compiler)

    // --- WorkManager ---
    implementation(libs.androidx.work.runtime)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.androidx.core)

    // --- Dependency Injection (Hilt) ---
    implementation(libs.com.google.dagger.hilt.android.gradle.plugin)
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.android.compiler)

    // --- Networking (Retrofit, Gson, Logging) ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // --- RxJava ---
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.rxjava3.retrofit.adapter)

    // --- Image Loading ---
    implementation(libs.glide)

    // --- Security ---
    implementation(libs.androidx.security.crypto)

    // --- Location Services ---
    implementation(libs.play.services.location)

    // --- Logging ---
    implementation(libs.timber)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.hamcrest)
    androidTestImplementation(libs.hamcrest.hamcrest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.awaitility)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestAnnotationProcessor(libs.hilt.android.compiler)

}