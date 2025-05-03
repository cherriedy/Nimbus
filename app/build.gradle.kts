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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY") ?: ""}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // Retrofit for network operations
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // RxJava for reactive programming
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    // RxJava adapter for Retrofit
    implementation(libs.rxjava3.retrofit.adapter)

    // Logging interceptor for logging network requests and responses
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rxjava3)
    implementation(libs.androidx.work.runtime)

    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Timber for logging
    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    annotationProcessor(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Lombok for getting rid of boilerplate code of getters, setters, constructors, etc.
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Hilt for dependency injection
    implementation(libs.com.google.dagger.hilt.android.gradle.plugin)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Glide for loading images with caching, transformation, etc.
    implementation(libs.glide)

    // Security for encrypting sensitive data
    implementation(libs.androidx.security.crypto)

    // Mockito for mocking in unit tests
    testImplementation(libs.mockito.core)

    // Google Play Services for location services
    implementation(libs.play.services.location)
}