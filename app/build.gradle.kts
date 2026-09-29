plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    namespace = "com.nestor.futbolia"

    compileSdk = 36

    defaultConfig {

        applicationId = "com.nestor.futbolia"

        minSdk = 24

        targetSdk = 36

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    kotlinOptions {

        jvmTarget = "11"
    }

    buildFeatures {

        compose = true
    }

    packaging {

        resources {

            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /*
     * ==========================
     * JETPACK COMPOSE
     * ==========================
     */

    implementation(
        platform(
            "androidx.compose:compose-bom:2026.06.01"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )


    /*
     * ==========================
     * ACTIVITY
     * ==========================
     */

    implementation(
        "androidx.activity:activity-compose:1.11.0"
    )


    /*
     * ==========================
     * LIFECYCLE
     * ==========================
     */

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.10.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )


    /*
     * ==========================
     * COROUTINES
     * ==========================
     */

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )


    /*
     * ==========================
     * TEST
     * ==========================
     */

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2026.06.01"
        )
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )
}
