plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    namespace = "com.nestor.futbolia"

    /*
     * AGP 8.6.1 tiene soporte máximo para API 35.
     */
    compileSdk = 35

    defaultConfig {

        applicationId = "com.nestor.futbolia"

        minSdk = 24

        targetSdk = 35

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

    /*
     * AGP 8.6 requiere JDK 17 para el entorno de compilación.
     * También dejamos el código Java/Kotlin en JVM 17.
     */
    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {

        jvmTarget = "17"
    }

    buildFeatures {

        compose = true
    }

    packaging {

        resources {

            excludes +=
                "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /*
     * ==============================
     * JETPACK COMPOSE
     * ==============================
     */

    implementation(
        platform(
            "androidx.compose:compose-bom:2026.06.01"
        )
    )

    androidTestImplementation(
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
     * ==============================
     * ACTIVITY
     * ==============================
     */

    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )


    /*
     * ==============================
     * LIFECYCLE
     * ==============================
     */

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.10.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )


    /*
     * ==============================
     * COROUTINES
     * ==============================
     */

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )


    /*
     * ==============================
     * HERRAMIENTAS DE DESARROLLO
     * ==============================
     */

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )


    /*
     * ==============================
     * PRUEBAS
     * ==============================
     */

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )
}
