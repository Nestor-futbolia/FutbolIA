plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android { namespace = "com.futbolia.app"; compileSdk = 35
    defaultConfig { applicationId = "com.futbolia.app"; minSdk = 24; targetSdk = 35; versionCode = 20; versionName = "2.0.0" }
}
